package dto;
import java.util.UUID;

public record BankAccountSummaryResponse(
        UUID accountId,
        String bankAccountType,
        String bankAccountStatus
) {}