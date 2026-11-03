package uz.uzkassa.smartpos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.FieldValidation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationField<T> {
    private T value;
    private Object wrongValue;
    private FieldValidation validation;

    public boolean isValid() {
        return FieldValidation.VALID.equals(validation);
    }

    public String getWrongStringValue() {
        return wrongValue == null ? "" : wrongValue.toString();
    }
}
