package com.petshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NewsletterRequest(
        @Email @NotBlank String email
) {
}
