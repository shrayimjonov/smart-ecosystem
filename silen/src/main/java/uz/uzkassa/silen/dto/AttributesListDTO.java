package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.ProductType;

import java.util.HashMap;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttributesListDTO {
    @NotNull
    Long id;

    Long parentId;

    @NotNull
    @Size(min = 1, max = 255, message = "Размер должен быть между 1 и 255")
    String name;

    @Size(max = 15, message = "Размер должен быть между 0 и 15")
    String prefix;

    @Size(max = 15, message = "Размер должен быть между 0 и 15")
    String postfix;

    @NotNull
    Set<ProductType> productTypes;

    Set<SelectItem> productTypeNames;

    HashMap<String, Object> additional;

    Set<AttributesListDTO> children;

    @NotNull
    boolean visible;

    int sorter;

}
