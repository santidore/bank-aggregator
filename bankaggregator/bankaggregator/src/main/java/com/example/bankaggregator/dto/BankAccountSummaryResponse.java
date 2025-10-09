package com.example.bankaggregator.dto;
import com.example.bankaggregator.enums.BankAccountStatus;
import com.example.bankaggregator.enums.BankAccountType;

import java.util.UUID;

public record BankAccountSummaryResponse(
        UUID accountId,
        BankAccountType bankAccountType,
        BankAccountStatus bankAccountStatus
) {}