package uz.uzkassa.smartpossupply.web.rest.errors;

import org.springframework.http.HttpStatus;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 15.08.2024
 */

public class SmartposIntegrationException extends AppGlobalException {

    public SmartposIntegrationException(String title) {
        this(title, null);
    }

    public SmartposIntegrationException(String title, String message) {
        super(title, message, HttpStatus.BAD_REQUEST);
    }
}
