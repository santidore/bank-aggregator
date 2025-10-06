package dto;

import enums.DocumentIdType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public record CreateUserRequest(

        @NotBlank(message = "Name must be provided.")
        @Size(max = 50, message = "Name can't be longer than 50 characters.")
        String name,

        @NotBlank(message = "Surname must be provided.")
        @Size(max = 50, message = "Surname can't be longer than 50 characters.")
        String surname,

        @Email(message = "Must be a valid email.")
        String email,

        @NotBlank(message = "Nationality must be provided")
        String nationality,

        @NotBlank(message = "Document ID Type must be provided")
        DocumentIdType documentIdType,

        @NotBlank(message = "Document ID Number must be provided")
        String documentIdNumber
) { }
