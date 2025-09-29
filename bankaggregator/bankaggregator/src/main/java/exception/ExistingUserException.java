package exception;

import org.springframework.data.jpa.repository.JpaRepository;

public class ExistingUserException extends RuntimeException {

    public ExistingUserException (String name, String surname) {
        super("Existing user found by name: " + name + " and surname: " + surname);
    }
}
