package com.andromeda.immicart.mpesa.model;

import com.andromeda.immicart.mpesa.constants.TransactionType;

public class LNMExpress {
    private String BusinessShortCode;
    private String PassKey;
    private String Password;
    private String TimeStamp;
    private String Amount;
    private String TransactionType;
    private String PartyA;
    private String PartyB;
    private String PhoneNumber;
    private String CallbackUrl;
    private TransactionType Type;
    private String AccountReference;
    private String TransactionDescription;

    public LNMExpress(String businessShortCode, String password, String timeStamp, String amount, String transactionType, String partyA, String partyB, String phoneNumber, String callbackUrl, String accountReference, String transactionDescription) {
        BusinessShortCode = businessShortCode;
//        PassKey = passKey;
        Password = password;
        TimeStamp = timeStamp;
        Amount = amount;
        TransactionType = transactionType;
        PartyA = partyA;
        PartyB = partyB;
        PhoneNumber = phoneNumber;
        CallbackUrl = callbackUrl;
        AccountReference = accountReference;
        TransactionDescription = transactionDescription;
    }

    public LNMExpress(String businessShortCode, String passKey, String timeStamp, String amount, String partyA, String partyB, String phoneNumber, String callbackUrl, TransactionType type, String accountReference, String transactionDescription) {
        BusinessShortCode = businessShortCode;
        PassKey = passKey;
        TimeStamp = timeStamp;
        Amount = amount;
        PartyA = partyA;
        PartyB = partyB;
        PhoneNumber = phoneNumber;
        CallbackUrl = callbackUrl;
        Type = type;
        AccountReference = accountReference;
        TransactionDescription = transactionDescription;
    }

    public String getBusinessShortCode() {
        return BusinessShortCode;
    }

    public void setBusinessShortCode(String businessShortCode) {
        BusinessShortCode = businessShortCode;
    }

    public String getPassKey() {
        return PassKey;
    }

    public void setPassKey(String passKey) {
        PassKey = passKey;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String transactionType) {
        TransactionType = transactionType;
    }

    public String getPartyA() {
        return PartyA;
    }

    public void setPartyA(String partyA) {
        PartyA = partyA;
    }

    public String getPartyB() {
        return PartyB;
    }

    public void setPartyB(String partyB) {
        PartyB = partyB;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getCallbackUrl() {
        return CallbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        CallbackUrl = callbackUrl;
    }

    public TransactionType getType() {
        return Type;
    }

    public void setType(TransactionType type) {
        Type = type;
    }

    public String getAccountReference() {
        return AccountReference;
    }

    public void setAccountReference(String accountReference) {
        AccountReference = accountReference;
    }

    public String getTransactionDescription() {
        return TransactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        TransactionDescription = transactionDescription;
    }
}
