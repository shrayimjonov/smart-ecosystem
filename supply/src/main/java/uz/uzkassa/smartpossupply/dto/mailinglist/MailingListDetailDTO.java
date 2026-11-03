package uz.uzkassa.smartpossupply.dto.mailinglist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailingListDetailDTO extends CommonDTO {

    String description;

    CommonStatus status;

    Long companyId;

    List<CompanyBaseDTO> customers;
}
