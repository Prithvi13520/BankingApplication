package com.app.banking.constants;

public class Constants {

    public static final String ACCOUNT_EXISTS_PAN_CODE = "001";
    public static final String ACCOUNT_EXISTS_MAIL_PHN_CODE = "002";
    public static final String ACCOUNT_EXISTS_MAIL_PHN_MESSAGE = "Already an account with same email id or mobile number present in system";
    public static final String ACCOUNT_EXISTS_PAN_MESSAGE = "Already an other account linked with same PAN in system";
    public static final String ACCOUNT_CREATION_SUCCESS_CODE = "200";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created";
    public static final String MAIL_EXCEPTION_CODE = "250";
    public static final String MAIL_EXCEPTION_MESSAGE = "Alert Mail Notification Failed";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with the provided Account Number not present in system, kindly reach out to Bank support!";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE = "Active account found in the system!";
    public static final String ACCOUNT_CREDIT_SUCCESS_CODE = "005";
    public static final String ACCOUNT_CREDIT_SUCCESS_MESSAGE = "Amount credit to the account successfuly!";
    public static final String ACCOUNT_BALANCE_LOW_CODE = "006";
    public static final String ACCOUNT_BALANCE_LOW_MESSAGE = "Source Account doesn't have sufficient balance for debit";
    public static final String ACCOUNT_DEBIT_SUCCESS_CODE = "007";
    public static final String ACCOUNT_DEBIT_SUCCESS_MESSAGE = "Debit succesfull kindly collect the amount!";
    public static final String CREDIT_ACCOUNT_NOT_EXIST_CODE = "008";
    public static final String CREDIT_ACCOUNT_NOT_EXIST_MESSAGE = "User with the provided Destination Account Number not present in system, kindly reach out to Bank support!";
    public static final String DEBIT_ACCOUNT_NOT_EXIST_CODE = "009";
    public static final String DEBIT_ACCOUNT_NOT_EXIST_MESSAGE = "User with the provided Source Account Number not present in system, kindly reach out to Bank support!";
    public static final String TRANSFER_ACCOUT_SUCCESS_CODE = "010";
    public static final String TRANSFER_ACCOUT_SUCCESS_MESSAGE = "Money Transfer Successful!";
    public static final String CREDIT = "credit";
    public static final String DEBIT = "debit";
    public static final String SUCCESS = "success";
    public static final String ACTIVE = "active";
    public static final String FILE = "C:\\Users\\prithviraj.subburam\\Downloads\\Learning\\Practice\\MyStatement.pdf";
    public static final String FILE_CREATION_SUCCESS_CODE = "011"; 
    public static final String FILE_CREATION_FAILURE_CODE = "012"; 
}
