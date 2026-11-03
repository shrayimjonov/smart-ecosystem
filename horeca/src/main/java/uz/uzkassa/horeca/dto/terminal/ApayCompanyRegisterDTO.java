package uz.uzkassa.horeca.dto.terminal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.activityttype.ActivityTypeDTO;
import uz.uzkassa.horeca.dto.address.AddressDTO;
import uz.uzkassa.horeca.dto.employee.OwnerDTO;
import uz.uzkassa.horeca.enums.ApayMerchantReferenceType;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.sync.BankSyncDTO;
import uz.uzkassa.horeca.enums.BusinessType;
import uz.uzkassa.horeca.enums.Vat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApayCompanyRegisterDTO {

    String name;

    String brandName;

    String tin;

    String phone;

    ApayMerchantReferenceType referenceType; //TODO: not null

    ActivityTypeDTO activityType;

    FileDTO cadastreRentFile; // TODO: not null

    FileDTO applicationFile; //TODO: not null

    BusinessType businessType;

    AddressDTO address;

    BankSyncDTO bank;

    BigDecimal vatRate;

    Vat vat;

    OwnerDTO owner;

    List<ApayBranchRegisterDTO> branchList = new ArrayList<>();

    String tariffId;
}
