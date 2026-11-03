package uz.uzkassa.smartpossupply.dto.aslbelgi.aic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AicEnrichmentErrorResponse {
    private String message;

    @Override
    public String toString() {
        return "Ошибка от TrueApi!\n" + message;
    }
}
