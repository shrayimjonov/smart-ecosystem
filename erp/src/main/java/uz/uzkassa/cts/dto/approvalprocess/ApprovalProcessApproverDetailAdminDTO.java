package uz.uzkassa.cts.dto.approvalprocess;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.ApproveType;
import uz.uzkassa.cts.enums.Role;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApprovalProcessApproverDetailAdminDTO implements Serializable {

    Long id;

    ApproveType type;

    Role role;

    CommonDTO employee;

    boolean approveForAll = false;
}
