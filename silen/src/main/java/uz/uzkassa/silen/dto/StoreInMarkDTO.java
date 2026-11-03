package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreInMarkDTO implements Serializable {

    String id;

    PackageType unit;

    String code;

    @NotNull(message = "Код не может быть пустым")
    String printCode;

    Long itemId;

    @NotNull(message = "Возврат ид не может быть пустым")
    String storeInId;
}
