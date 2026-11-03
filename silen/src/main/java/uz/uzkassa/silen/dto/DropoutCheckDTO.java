package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.silen.enumeration.CodeType;

@Getter
@Setter
@NoArgsConstructor
public class DropoutCheckDTO {
    String printCode;

    String parentCode;

    CodeType packageType;

    String packageTypeName;

    ProductCommonDTO product;
}
