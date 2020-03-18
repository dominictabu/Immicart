package com.andromeda.immicart.delivery.wallet.C2BService.model;

import com.google.gson.annotations.SerializedName;

public class RegisterURL {

    @SerializedName("ShortCode")
    private String shortCode;
    @SerializedName("ResponseType")
    private String responseType;
    @SerializedName("ConfirmationURL")
    private String confirmationURL;
    @SerializedName("ValidationURL")
    private String validationURL;


    public RegisterURL(String shortCode, String responseType, String confirmationURL, String validationURL) {
        this.shortCode = shortCode;
        this.responseType = responseType;
        this.confirmationURL = confirmationURL;
        this.validationURL = validationURL;
    }
}