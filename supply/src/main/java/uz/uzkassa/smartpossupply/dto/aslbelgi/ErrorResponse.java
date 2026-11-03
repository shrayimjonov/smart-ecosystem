package uz.uzkassa.smartpossupply.dto.aslbelgi;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse implements Serializable {
    String error_message;

    @Override
    public String toString() {
        return "Ошибка от TrueApi!\n" + error_message;
    }
}
