package uz.uzkassa.silen.dto;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.CodeType;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrintTemplateDTO {

    String id;

    String name;

    String productId;

    ProductCommonDTO product;

    String template;

    SelectItem organization;

    CodeType codeType;
}
