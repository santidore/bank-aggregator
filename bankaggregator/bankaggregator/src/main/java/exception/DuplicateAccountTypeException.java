package exception;

import enums.BankAccountType;

import java.util.UUID;

public class DuplicateAccountTypeException extends RuntimeException {

    public DuplicateAccountTypeException(UUID userId, BankAccountType bankAccountType) {
        super("The userId " + userId + " already has an existing " + bankAccountType + " account.");
    }

}
