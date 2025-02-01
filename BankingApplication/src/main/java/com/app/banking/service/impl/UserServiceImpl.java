package com.app.banking.service.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.banking.constants.Constants;
import com.app.banking.dto.AccountInfo;
import com.app.banking.dto.BankResponse;
import com.app.banking.dto.EmailDetails;
import com.app.banking.dto.UserRequest;
import com.app.banking.entity.User;
import com.app.banking.repository.UserRepository;
import com.app.banking.utils.AccountUtils;

@Service
public class UserServiceImpl implements UserService{

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

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
        .status("ACTIVE")
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

}
