package com.app.banking.service.impl;

import com.app.banking.dto.BankResponse;
import com.app.banking.dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
}
