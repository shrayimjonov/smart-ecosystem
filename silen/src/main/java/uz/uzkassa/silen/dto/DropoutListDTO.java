package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.CodeType;
import uz.uzkassa.silen.enumeration.DropoutReason;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DropoutListDTO {

    String id;

    String printCode;

    String codeId;

    CodeType codeType;

    ProductCommonDTO product;

    DropoutReason dropoutReason;

    String dropoutReasonNameRu;

    String createdBy;

    LocalDateTime createdDate;

    String reportId;

    int amount;
}
