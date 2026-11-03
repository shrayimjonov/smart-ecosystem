package uz.uzkassa.silen.exceptions;

public final class ErrorConstants {

    public static final String ERR_CREDENTIALS = "error.credentials";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String ERR_METHOD_NOT_SUPPORTED = "error.methodNotSupported";
    public static final String ERR_INTERNAL_SERVER_ERROR = "error.internalServerError";
    public static final String ERR_CONNECTION_ERROR = "error.connectionError";
    public static final String ERR_INTERNAL_UNRECOGNIZED = "error.unrecognized";
    public static final String ERR_INTERNAL_DESERIALIZATION = "error.deserialization";
    public static final int DEFAULT_ERROR_CODE = -1;
    public static final int MARK_USED_ERROR_CODE = -2;
    public static final int AGGREGATION_USED_ERROR_CODE = -3;
    public static final int AGGREGATION_REGISTERED_IN_SILEN_ERROR_CODE = -4;
    public static final int TURON_NOT_AVAILABLE_ERROR_CODE = -5;
    public static final int BILLING_ERROR_CODE = -6;
    public static final int ASILBELGI_ERROR_CODE = -7;
    public static final int TURON_AGGREGATION_NOT_FOUND = -8;

    private ErrorConstants() {
    }
}
