package dto;

import enums.BankAccountStatus;
import enums.BankAccountType;

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
