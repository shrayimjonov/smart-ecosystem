package uz.uzkassa.smartpossupply.web.rest.errors;

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
