package dto;
import java.util.UUID;

public record CreateUserResponse(
        UUID id,
        String name,
        String surname,
        String nationality

) { }
