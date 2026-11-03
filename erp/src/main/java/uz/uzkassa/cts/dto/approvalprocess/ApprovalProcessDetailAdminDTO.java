package uz.uzkassa.cts.dto.approvalprocess;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApprovalProcessDetailAdminDTO implements Serializable {

    Long id;

    EntityTypeEnum entityType;

    CompanyCommonDTO company;

    List<ApprovalProcessStepDetailAdminDTO> steps;
}
