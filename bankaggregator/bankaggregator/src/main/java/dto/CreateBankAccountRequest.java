package dto;

import enums.BankAccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateBankAccountRequest(
        @NotNull(message = "User ID is required")
        UUID userId,

        @NotNull(message = "Initial balance is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Initial balance must be >= 0")
        BigDecimal initialBalance,

        @NotNull(message = "Bank account type is required")
        BankAccountType bankAccountType
) { }
