package com.devon.building.enums;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public enum Transaction {
    CSKH("Chăm sóc khách hàng"),
    DDX("Dẫn đi xem");
    private final String transaction;
    Transaction(String s) {
        this.transaction = s;
    }
    public static Map<String,String> getTransactionMap(){
        Map<String, String> transactionMap = new LinkedHashMap<>();
        for(Transaction transaction:Transaction.values()){
            transactionMap.put(transaction.toString(), transaction.transaction);
        }
        return transactionMap;
    }
}
