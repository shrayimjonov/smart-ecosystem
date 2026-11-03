package uz.uzkassa.cts.web.rest.errors;

public class JobSchedulingException extends RuntimeException {
    public JobSchedulingException(String message, Throwable cause) {
        super(message, cause);
    }
}