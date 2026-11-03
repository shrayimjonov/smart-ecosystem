package uz.uzkassa.horeca.web.rest.billing;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import uz.uzkassa.horeca.web.rest.errors.AppGlobalException;


public class BillingBaseResource {

    protected AppGlobalException getException(Exception ex) {
        if (ex instanceof ResponseStatusException responseStatusException) {
            return new AppGlobalException(responseStatusException.getReason(), responseStatusException.getMessage(), HttpStatus.valueOf(responseStatusException.getStatusCode().value()));
        } else {
            int statusCode = StringUtils.isNumeric(ex.getMessage().substring(0, 3)) ? Integer.parseInt(ex.getMessage().substring(0, 3)) : HttpStatus.INTERNAL_SERVER_ERROR.value();
            HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
            if (HttpStatus.BAD_REQUEST.equals(httpStatus)) {
                return new AppGlobalException("Bad request", ex.getMessage(), HttpStatus.BAD_REQUEST);
            } else if (HttpStatus.NOT_FOUND.equals(httpStatus)) {
                return new AppGlobalException("Entity not found", ex.getMessage(), HttpStatus.NOT_FOUND);
            } else {
                return new AppGlobalException("Internal Server Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
