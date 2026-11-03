package uz.uzkassa.smartpossupply.web.rest.errors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TuronErrorResponse {

    List<TuronProtobeansError> fieldErrors;

    List<TuronGlobalError> globalErrors;

    boolean success;

    @Override
    public String toString() {
        StringBuilder errorMessage = new StringBuilder("Ошибка от Турона!");
        if (!CollectionUtils.isEmpty(globalErrors)) {
            for (int i = 0; i < globalErrors.size(); i++) {
                if (globalErrors.size() > 1) {
                    errorMessage.append("\n").append(i + 1).append(") ");
                    errorMessage.append(getGlobalErrors().get(i));
                } else {
                    errorMessage.append("\n");
                    errorMessage.append(getGlobalErrors().get(i));
                }
            }
        }
        return errorMessage.toString();
    }
}
