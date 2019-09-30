package com.andromeda.immicart.mpesa.constants;

public enum TransactionType {

    CustomerPayBillOnline {
        @Override
        public String toString() {
            return TransType.TRANSACTION_TYPE_CUSTOMER_PAYBILL_ONLINE;
        }
    },

    CustomerBuyGoodsOnline{
        @Override
        public String toString() {
            return TransType.TRANSACTION_TYPE_CUSTOMER_BUY_GOODS;
        }
    }
}
