package tn.iteam.authservice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record AdminCreateUserRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        Set<String> roles
) {
}
