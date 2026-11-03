package uz.uzkassa.smartpossupply.integration.billing;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingIntegrationException extends RuntimeException {
    BillingExceptionDTO dto;

    public BillingIntegrationException(BillingExceptionDTO dto) {
        super(dto.getMessage());
        this.dto = dto;
    }
}