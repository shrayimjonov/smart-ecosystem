package uz.uzkassa.smartpossupply.dto.loghistory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.06.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogHistoryListDTO {

    String id;

    EntityTypeEnum entityType;

    String field;

    String fromValue;

    String toValue;

    LocalDateTime updateDate;

    CommonDTO updater;

    CompanyBaseDTO company;

    String universalNumber;
}
