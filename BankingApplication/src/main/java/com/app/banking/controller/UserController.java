package com.app.banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    UserService userService;

    @PostMapping("/createAccount")
    public BankResponse createAccount(@Valid @RequestBody UserRequest userRequest)
    {
        return userService.createAccount(userRequest);
    }

}
