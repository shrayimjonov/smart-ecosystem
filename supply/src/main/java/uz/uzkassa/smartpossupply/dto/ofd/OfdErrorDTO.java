package uz.uzkassa.smartpossupply.dto.ofd;

import java.io.Serializable;

public class OfdErrorDTO implements Serializable {

    private String field;
    private String message;

    public OfdErrorDTO() {}

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
