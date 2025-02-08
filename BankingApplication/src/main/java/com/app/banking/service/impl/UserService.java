package com.app.banking.service.impl;

import com.app.banking.dto.BankResponse;
import com.app.banking.dto.CreditDebitRequest;
import com.app.banking.dto.EnquiryRequest;
import com.app.banking.dto.TransferRequest;
import com.app.banking.dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transferAccount(TransferRequest request);
    BankResponse generateStatement(String accountNumber, String startDate, String endDate);
}
