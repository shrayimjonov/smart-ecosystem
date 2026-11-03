package uz.uzkassa.smartpossupply.web.rest.errors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TuronGlobalError {

    String error;

    int errorCode;

    public TuronGlobalError() {}

    public TuronGlobalError(String error, int errorCode) {
        this.error = error;
        this.errorCode = errorCode;
    }

    public TuronGlobalError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return error + ".";
    }
}
