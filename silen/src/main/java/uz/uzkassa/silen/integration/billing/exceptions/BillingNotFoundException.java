package uz.uzkassa.silen.integration.billing.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import uz.uzkassa.silen.exceptions.EntityNotFoundException;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entity not found")
public class BillingNotFoundException extends EntityNotFoundException {
    public BillingNotFoundException(String message) {
        super(message);
    }
}
