package com.app.banking.service.impl;

import java.io.FileNotFoundException;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.banking.constants.Constants;
import com.app.banking.dto.AccountInfo;
import com.app.banking.dto.BankResponse;
import com.app.banking.dto.CreditDebitRequest;
import com.app.banking.dto.EmailDetails;
import com.app.banking.dto.EnquiryRequest;
import com.app.banking.dto.TransactionDto;
import com.app.banking.dto.TransferRequest;
import com.app.banking.dto.UserRequest;

import com.app.banking.entity.User;
import com.app.banking.repository.UserRepository;
import com.app.banking.utils.AccountUtils;
import com.app.banking.utils.BankStatementUtil;
import com.itextpdf.text.DocumentException;

@Service
public class UserServiceImpl implements UserService{

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    BankStatementUtil bankStatementUtil;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * Creating an account - saving a new userinto db
         * chceck if user already has an account
         */
        logger.info("PanNumber validation starts");
        if(userRepository.existsByPanNumber(userRequest.getPanNumber().toUpperCase())){
            return BankResponse.builder()
            .responseCode(Constants.ACCOUNT_EXISTS_PAN_CODE)
            .responseMessage(Constants.ACCOUNT_EXISTS_PAN_MESSAGE)
            .accountInfo(null)
            .build();
        }

        logger.info("Email-Phonenumber validation starts");
        if(userRepository.existsByEmail(userRequest.getEmail().toLowerCase()) || userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())){
            return BankResponse.builder()
            .responseCode(Constants.ACCOUNT_EXISTS_MAIL_PHN_CODE)
            .responseMessage(Constants.ACCOUNT_EXISTS_MAIL_PHN_MESSAGE)
            .accountInfo(null)
            .build();
        }

        User newUser = User.builder()
        .firstName(userRequest.getFirstName())
        .lastName(userRequest.getLastName())
        .otherName(userRequest.getOtherName())
        .gender(userRequest.getGender())
        .address(userRequest.getAddress())
        .stateOfOrigin(userRequest.getStateOfOrigin())
        .accountNumber(AccountUtils.generateAccountNumber())
        .accountBalance(BigDecimal.ZERO)
        .email(userRequest.getEmail().toLowerCase())
        .phoneNumber(userRequest.getPhoneNumber())
        .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
        .panNumber(userRequest.getPanNumber().toUpperCase())
        .status(Constants.ACTIVE)
        .build();

        logger.info("User Entity object to be saved in DB is"+newUser.toString());

        User savedUser = userRepository.save(newUser);
        //Send Email Alert
        EmailDetails emailDetails = EmailDetails.builder()
        .recipient(savedUser.getEmail())
        .subject("ACCOUNT CREATION")
        .messageBody("Congratulations! your account has been successfully created.\n Your Account Details: \n"+
        "Account  Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + "\nAccount Number: "+ savedUser.getAccountNumber())
        .build();
        emailService.sendEmailAlert(emailDetails);

        logger.info("User record saved in DB"+savedUser.toString());

       return BankResponse.builder()
       .responseCode(Constants.ACCOUNT_CREATION_SUCCESS_CODE)
       .responseMessage(Constants.ACCOUNT_CREATION_MESSAGE)
       .accountInfo(
        AccountInfo.builder()
       .accountBalance(savedUser.getAccountBalance())
       .accountName(savedUser.getFirstName()+ " " + savedUser.getLastName() + " " +savedUser.getOtherName())
       .accountNumber(savedUser.getAccountNumber())
       .build()
       )
       .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        //Check if the provided account number exist in system
        boolean isAccountExist=userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!isAccountExist)
        {
            return BankResponse.builder()
            .responseCode(Constants.ACCOUNT_NOT_EXIST_CODE)
            .responseMessage(Constants.ACCOUNT_NOT_EXIST_MESSAGE)
            .accountInfo(null)
            .build();
        }
        else{
            User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
            return BankResponse.builder()
            .responseCode(Constants.ACCOUNT_FOUND_CODE)
            .responseMessage(Constants.ACCOUNT_FOUND_MESSAGE)
            .accountInfo(AccountInfo.builder()
            .accountName(foundUser.getFirstName()+" "+foundUser.getLastName())
            .accountNumber(foundUser.getAccountNumber())
            .accountBalance(foundUser.getAccountBalance())
            .build())
            .build();
        }

    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExist=userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!isAccountExist)
        {
            return Constants.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        else{
            User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
            return foundUser.getFirstName()+" "+foundUser.getLastName();
        }
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        boolean isAccountExist=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist)
        {
            return BankResponse.builder()
            .responseCode(Constants.ACCOUNT_NOT_EXIST_CODE)
            .responseMessage(Constants.ACCOUNT_NOT_EXIST_MESSAGE)
            .accountInfo(null)
            .build();
        }
        else{
            User creditUser = userRepository.findByAccountNumber(request.getAccountNumber());
            creditUser.setAccountBalance(creditUser.getAccountBalance().add(request.getAmount()));
            userRepository.save(creditUser);

            //send credit mail alert
            EmailDetails emailDetails = EmailDetails.builder()
            .recipient(creditUser.getEmail())
            .subject("CREDIT ALERT!")
            .messageBody("Your account has been credited with "+request.getAmount() +" rupees"+"\n Your Account Details: \n"+
            "Account  Name: " + creditUser.getFirstName() + " " + creditUser.getLastName() + "\nAccount Number: "+ creditUser.getAccountNumber())
            .build();
             emailService.sendEmailAlert(emailDetails);

             //transaction auditing
             TransactionDto transactionDto = TransactionDto.builder()
             .accountNumber(creditUser.getAccountNumber())
             .transactionType(Constants.CREDIT)
             .amount(request.getAmount())
             .build();
             transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
            .responseCode(Constants.ACCOUNT_CREDIT_SUCCESS_CODE)
            .responseMessage(Constants.ACCOUNT_CREDIT_SUCCESS_MESSAGE)
            .accountInfo(AccountInfo.builder()
            .accountBalance(creditUser.getAccountBalance())
            .accountName(creditUser.getFirstName()+" "+creditUser.getLastName())
            .accountNumber(creditUser.getAccountNumber())
            .build())
            .build();
        }
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        boolean isAccountExist=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist)
        {
            return BankResponse.builder()
            .responseCode(Constants.ACCOUNT_NOT_EXIST_CODE)
            .responseMessage(Constants.ACCOUNT_NOT_EXIST_MESSAGE)
            .accountInfo(null)
            .build();
        }
        else{
            User debitUser = userRepository.findByAccountNumber(request.getAccountNumber());
            int compare = debitUser.getAccountBalance().compareTo(request.getAmount());
            if(compare>=0){
            debitUser.setAccountBalance(debitUser.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(debitUser);

            //send debit mail alert
            EmailDetails emailDetails = EmailDetails.builder()
            .recipient(debitUser.getEmail())
            .subject("DEBIT ALERT!")
            .messageBody("Your account has been debited with "+request.getAmount() +" rupees"+"\n Your Account Details: \n"+
            "Account  Name: " + debitUser.getFirstName() + " " + debitUser.getLastName() + "\nAccount Number: "+ debitUser.getAccountNumber())
            .build();
            emailService.sendEmailAlert(emailDetails);
            
            //transaction auditing
            TransactionDto transactionDto = TransactionDto.builder()
            .accountNumber(debitUser.getAccountNumber())
            .transactionType(Constants.DEBIT)
            .amount(request.getAmount())
            .build();
            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
            .responseCode(Constants.ACCOUNT_DEBIT_SUCCESS_CODE)
            .responseMessage(Constants.ACCOUNT_DEBIT_SUCCESS_MESSAGE)
            .accountInfo(AccountInfo.builder()
            .accountBalance(debitUser.getAccountBalance())
            .accountName(debitUser.getFirstName()+" "+debitUser.getLastName())
            .accountNumber(debitUser.getAccountNumber())
            .build())
            .build();
            }
            else{
                return BankResponse.builder()
                .responseCode(Constants.ACCOUNT_BALANCE_LOW_CODE)
                .responseMessage(Constants.ACCOUNT_BALANCE_LOW_MESSAGE)
                .accountInfo(null)
                .build();
            }
        }
    }

    @Override
    public BankResponse transferAccount(TransferRequest request) {
        //Check if the destination account exists
        boolean isCreditAccountExist=userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if(!isCreditAccountExist)
        {
            return BankResponse.builder()
            .responseCode(Constants.CREDIT_ACCOUNT_NOT_EXIST_CODE)
            .responseMessage(Constants.CREDIT_ACCOUNT_NOT_EXIST_MESSAGE)
            .accountInfo(null)
            .build();
        }
        //Create a debit request for debiting source account number
        CreditDebitRequest debitRequest = new CreditDebitRequest();
        debitRequest.setAccountNumber(request.getSourceAccountNumber());
        debitRequest.setAmount(request.getAmount());
        //Debit operation invoked
        BankResponse debitResponse = debitAccount(debitRequest);
        //Debit account success flow
        if(debitResponse.getResponseCode()==Constants.ACCOUNT_DEBIT_SUCCESS_CODE)
        {
            //Create credit request
            CreditDebitRequest creditRequest = new CreditDebitRequest();
            creditRequest.setAccountNumber(request.getDestinationAccountNumber());
            creditRequest.setAmount(request.getAmount());
            //credit account invoke
            BankResponse creditResponse = creditAccount(creditRequest);
            //credit account success flow
            if(creditResponse.getResponseCode()==Constants.ACCOUNT_CREDIT_SUCCESS_CODE)
            {
                return BankResponse.builder()
                .responseCode(Constants.TRANSFER_ACCOUT_SUCCESS_CODE)
                .responseMessage(Constants.TRANSFER_ACCOUT_SUCCESS_MESSAGE)
                .accountInfo(debitResponse.getAccountInfo())
                .build();
            }
            //credit account failure flow(Anyway this flow will not happen as already precheck is done for credit account)
            if(creditResponse.getResponseCode()==Constants.ACCOUNT_NOT_EXIST_CODE)
            {
                creditResponse.setResponseCode(Constants.CREDIT_ACCOUNT_NOT_EXIST_CODE);
                creditResponse.setResponseMessage(Constants.CREDIT_ACCOUNT_NOT_EXIST_MESSAGE);
            }
            return creditResponse;
        }
        //Debit account failure flow
        else
        {
            if(debitResponse.getResponseCode()==Constants.ACCOUNT_NOT_EXIST_CODE)
            {
                debitResponse.setResponseCode(Constants.DEBIT_ACCOUNT_NOT_EXIST_CODE);
                debitResponse.setResponseMessage(Constants.DEBIT_ACCOUNT_NOT_EXIST_MESSAGE);
            }
            return debitResponse;
            
        }
    
    }

    @Override
    public BankResponse generateStatement(String accountNumber, String startDate, String endDate) {

        boolean isAccountExist=userRepository.existsByAccountNumber(accountNumber);

        if(!isAccountExist)
        {
            return BankResponse.builder()
            .responseCode(Constants.ACCOUNT_NOT_EXIST_CODE)
            .responseMessage(Constants.ACCOUNT_NOT_EXIST_MESSAGE)
            .accountInfo(null)
            .build();
        }
        else{
            User user = userRepository.findByAccountNumber(accountNumber);

            try {
                bankStatementUtil.generateStatement(accountNumber, startDate, endDate);
            } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
            return BankResponse.builder()
            .responseCode(Constants.FILE_CREATION_FAILURE_CODE)
            .responseMessage(e.getMessage())
            .accountInfo(null)
            .build();
            }

            return BankResponse.builder()
            .responseCode(Constants.FILE_CREATION_SUCCESS_CODE)
            .responseMessage("Statement from "+startDate+" to "+endDate+" will be sent in few minutes to your registered mail! kindly check the inbox")
            .accountInfo(AccountInfo.builder()
            .accountBalance(user.getAccountBalance())
            .accountName(user.getFirstName()+" "+user.getLastName())
            .accountNumber(user.getAccountNumber())
            .build())
            .build();
        }
    }

}
