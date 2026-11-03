package uz.uzkassa.smartpossupply.dto.stockadjustment;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.DocumentType;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdjustmentDocumentDTO implements Serializable {

    String number;

    LocalDate date;

    DocumentType type;
}
