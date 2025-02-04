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
import com.app.banking.dto.UserRequest;
import com.app.banking.service.impl.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/bank/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @PostMapping("/createAccount")
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
    

}
