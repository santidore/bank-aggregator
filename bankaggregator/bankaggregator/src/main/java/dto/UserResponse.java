package dto;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String surname,
        String nationality

) { }
