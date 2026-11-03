package uz.uzkassa.horeca.web.rest.errors;

import org.springframework.http.HttpStatus;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.07.2024
 */

public class ApayIntegrationException extends AppGlobalException {

    public ApayIntegrationException(String title) {
        this(title, null);
    }

    public ApayIntegrationException(String title, String message) {
        super(title, message, HttpStatus.BAD_REQUEST);
    }
}
