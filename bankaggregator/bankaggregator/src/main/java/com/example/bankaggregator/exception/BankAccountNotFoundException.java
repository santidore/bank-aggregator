package com.example.bankaggregator.exception;

import java.util.UUID;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException(UUID accountId){
        super("Account not found with id: " + accountId);
    }
}
