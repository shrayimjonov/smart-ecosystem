package uz.uzkassa.silen.exceptions;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TuronProtobeansError {

    String fieldError;

    String fieldName;

    @Override
    public String toString() {
        return "\n" + fieldError;
    }
}
