package com.app.banking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.banking.dto.BankResponse;
import com.app.banking.dto.CreditDebitRequest;
import com.app.banking.dto.EnquiryRequest;
import com.app.banking.dto.TransferRequest;
import com.app.banking.dto.UserRequest;
import com.app.banking.service.impl.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/bank/user")
@Tag(name = "User Account Management APIs")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @PostMapping("/createAccount")
    @Operation(
    summary = "Create New User Account",
    description = "Creating a new user and assigning a account ID"
    )
    @ApiResponse(
        responseCode = "201",
        description = "http status 201 created"
    )
    public ResponseEntity<BankResponse> createAccount(@Valid @RequestBody UserRequest userRequest)
    {
        logger.info("Entered create account endpoint");
        BankResponse bankResponse = userService.createAccount(userRequest); 
        logger.info("response recieved from create account endpoint");
        if(bankResponse.getResponseCode()=="200")
        {
            logger.info("Account created successfully with account number "+bankResponse.getAccountInfo().getAccountNumber());
            return new ResponseEntity<>(bankResponse, HttpStatus.OK);
        }   
        else
        {
            logger.info("There is some problem with creating an account, kindly go through the logs for more information");
            return new ResponseEntity<>(bankResponse, HttpStatus.NOT_ACCEPTABLE);
        }   
    }

    @GetMapping("/balanceEnquiry")
    @Operation(
        summary = "Balance Enquiry",
        description = "To check the balance of single account"
        )
        @ApiResponse(
            responseCode = "200",
            description = "http status 200 SUCCESS"
        )
    public ResponseEntity<BankResponse> getBalanceEnquiry(@RequestBody EnquiryRequest request) {
        BankResponse bankResponse = userService.balanceEnquiry(request);
        if(bankResponse.getResponseCode()=="004")
        {
            return new ResponseEntity<>(bankResponse,HttpStatus.FOUND);
        }
        else
        {
            return new ResponseEntity<>(bankResponse,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/nameEnquiry")
    public ResponseEntity<String> getNameEnquiry(@RequestBody EnquiryRequest request) {
        String responseString = userService.nameEnquiry(request);
        if(responseString.contains("Account Number not present in system"))
        {
            return new ResponseEntity<>(responseString,HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<>(responseString,HttpStatus.FOUND);
        }
    }

    @PostMapping("/creditAccount")
    public ResponseEntity<BankResponse> creditAccount(@RequestBody CreditDebitRequest request) {
        BankResponse bankResponse = userService.creditAccount(request);
        if (bankResponse.getResponseCode()=="005") {
            return new ResponseEntity<>(bankResponse, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(bankResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/debitAccount")
    public ResponseEntity<BankResponse> debitAccount(@RequestBody CreditDebitRequest request) {
        BankResponse bankResponse = userService.debitAccount(request);
        if (bankResponse.getResponseCode()=="007") {
            return new ResponseEntity<>(bankResponse, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(bankResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/transferAccount")
    public ResponseEntity<BankResponse> transferAccount(@RequestBody TransferRequest request) {
        BankResponse bankResponse = userService.transferAccount(request);
        if (bankResponse.getResponseCode()=="010") {
            return new ResponseEntity<>(bankResponse, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(bankResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/generateStatement")
    public ResponseEntity<BankResponse> getStatement(@RequestParam String accountNumber, @RequestParam String startDate, @RequestParam String endDate)  {
        BankResponse bankResponse =  userService.generateStatement(accountNumber, startDate, endDate);

        if(bankResponse.getResponseCode()=="011")
        {
            return new ResponseEntity<>(bankResponse, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(bankResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    

}
