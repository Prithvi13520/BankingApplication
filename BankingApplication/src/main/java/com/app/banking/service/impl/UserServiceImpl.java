package com.app.banking.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.banking.Constants.Constants;
import com.app.banking.dto.AccountInfo;
import com.app.banking.dto.BankResponse;
import com.app.banking.dto.UserRequest;
import com.app.banking.entity.User;
import com.app.banking.repository.UserRepository;
import com.app.banking.utils.AccountUtils;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * Creating an account - saving a new userinto db
         * chceck if user already has an account
         */
        if(userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
            .responseCode(Constants.ACCOUNT_EXISTS_CODE)
            .responseMessage(Constants.ACCOUNT_EXISTS_MESSAGE)
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
        .email(userRequest.getEmail())
        .phoneNumber(userRequest.getPhoneNumber())
        .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
        .status("ACTIVE")
        .build();
        User savedUser = userRepository.save(newUser);

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
