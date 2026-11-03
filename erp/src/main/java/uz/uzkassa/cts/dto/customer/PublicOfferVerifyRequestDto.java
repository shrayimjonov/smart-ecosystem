package uz.uzkassa.cts.dto.customer;

import jakarta.validation.constraints.NotNull;

public record PublicOfferVerifyRequestDto(@NotNull Long customerId, @NotNull String code) {
}
