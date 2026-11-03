package uz.uzkassa.horeca.dto.loghistory;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.enums.EntityTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
public class LogHistoryListDTO {

    String id;

    EntityTypeEnum entityType;

    String field;

    String fromValue;

    String toValue;

    LocalDateTime updateDate;

    CommonDTO updater;

    CompanyCommonDTO company;
}
