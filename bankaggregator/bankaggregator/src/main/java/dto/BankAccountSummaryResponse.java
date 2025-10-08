package dto;
import enums.BankAccountStatus;
import enums.BankAccountType;

import java.util.UUID;

public record BankAccountSummaryResponse(
        UUID accountId,
        BankAccountType bankAccountType,
        BankAccountStatus bankAccountStatus
) {}