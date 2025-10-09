package com.example.bankaggregator.dto;

import com.example.bankaggregator.enums.BankAccountStatus;
import com.example.bankaggregator.enums.BankAccountType;

import java.math.BigDecimal;
import java.util.UUID;

public record BankAccountDetailResponse(
        UUID accountId,
        BankAccountType bankAccountType,
        BankAccountStatus bankAccountStatus,
        BigDecimal balance,
        String iban,
        String swift
) { }
