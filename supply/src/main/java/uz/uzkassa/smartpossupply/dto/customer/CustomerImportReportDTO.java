package uz.uzkassa.smartpossupply.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerImportReportDTO extends BaseImportReportDTO {

    String cellValue1;

    String cellValue2;

    String cellValue3;

    String cellValue4;

    String cellValue5;
}
