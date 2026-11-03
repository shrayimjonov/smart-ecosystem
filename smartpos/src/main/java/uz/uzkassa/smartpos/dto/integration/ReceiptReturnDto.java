package uz.uzkassa.smartpos.dto.integration;

import jakarta.validation.constraints.NotBlank;

public record ReceiptReturnDto(@NotBlank String uid) {
}
