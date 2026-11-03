package uz.uzkassa.smartpossupply.web.rest.errors;

public class JobSchedulingException extends RuntimeException {
    public JobSchedulingException(String message, Throwable cause) {
        super(message, cause);
    }
}