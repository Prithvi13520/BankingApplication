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
import com.app.banking.dto.UserRequest;
import com.app.banking.service.impl.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;


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

}
