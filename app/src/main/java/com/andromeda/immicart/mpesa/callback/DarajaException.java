package com.andromeda.immicart.mpesa.callback;

import com.andromeda.immicart.mpesa.model.ErrorResponse;

public  class DarajaException extends Exception{

    ErrorResponse errorResponse;

    public DarajaException() {
        super();
    }

    public DarajaException(String message) {
        super(message);
    }


    public DarajaException(ErrorResponse errorResponse) {
        super(errorResponse.getCode() + " : " + errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }

    public DarajaException(String message, Throwable cause) {
        super(message, cause);
    }

    public DarajaException(Throwable cause) {
        super(cause);
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

}