package uz.uzkassa.cts.dto.customer;

import jakarta.validation.constraints.NotNull;

public record UpdateCustomerAPayTariffDTO(@NotNull Long customerId, @NotNull String tariffId) {
}
