package com.andromeda.immicart.delivery.wallet.C2BService.model;

import com.google.gson.annotations.SerializedName;

public class C2B {

    @SerializedName("ShortCode")
    private String shortCode;
    @SerializedName("CommandID")
    private String commandID;
    @SerializedName("Amount")
    private String amount;
    @SerializedName("Msisdn")
    private String msisdn;
    @SerializedName("BillRefNumber")
    private String billRefNumber;


    public C2B(String shortCode, String commandID, String amount, String msisdn, String billRefNumber) {
        this.shortCode = shortCode;
        this.commandID = commandID;
        this.amount = amount;
        this.msisdn = msisdn;
        this.billRefNumber = billRefNumber;
    }
}


