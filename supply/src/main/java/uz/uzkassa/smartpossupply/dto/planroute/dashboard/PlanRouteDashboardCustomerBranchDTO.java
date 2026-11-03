package uz.uzkassa.smartpossupply.dto.planroute.dashboard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.address.AddressDetailDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;

import java.math.BigDecimal;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteDashboardCustomerBranchDTO extends CommonDTO {

    AddressDetailDTO address;

    FileDTO photo;

    String phone;

    BigDecimal currentBalance;
}
