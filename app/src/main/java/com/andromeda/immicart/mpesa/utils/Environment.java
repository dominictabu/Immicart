package com.andromeda.immicart.mpesa.utils;

import com.andromeda.immicart.mpesa.network.URLs;

public enum Environment {

    SANDBOX {
        @Override
        public String toString() {
            return URLs.SANDBOX_BASE_URL;
        }
    },


    PRODUCTION {
        @Override
        public String toString() {
            return URLs.PRODUCTION_BASE_URL;
        }
    }
}
