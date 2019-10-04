package com.andromeda.immicart.checkout.common

enum class Status {
    SUCCESS,
    ERROR,
    LOADING;

    val isLoading: Status
        get() = LOADING
}