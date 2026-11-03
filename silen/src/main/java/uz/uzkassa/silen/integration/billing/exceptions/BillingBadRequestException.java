package uz.uzkassa.silen.integration.billing.exceptions;

import lombok.Getter;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.exceptions.ErrorConstants;

@Getter
public class BillingBadRequestException extends BadRequestException {
    public BillingBadRequestException(String message) {
        super(message, ErrorConstants.DEFAULT_ERROR_CODE);

    }
}
