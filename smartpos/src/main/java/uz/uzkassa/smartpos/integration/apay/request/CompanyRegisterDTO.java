package uz.uzkassa.smartpos.integration.apay.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.integration.apay.*;
import uz.uzkassa.smartpos.integration.enums.BusinessTypeEnum;
import uz.uzkassa.smartpos.integration.enums.MerchantReferenceType;
import uz.uzkassa.smartpos.integration.enums.Vat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class CompanyRegisterDTO implements Serializable {
    @NotNull
    @NotEmpty
    String name;

    String brandName;

    @NotNull
    @Size(min = 9, max = 14)
    String tin;

    @NotNull
    @NotEmpty
    String phone;

    @NotNull
    MerchantReferenceType referenceType;

    @NotNull
    ActivityTypeDTO activityType;

    @NotNull
    FileDTO cadastreRentFile; // fixme need to remove

    @NotNull
    FileDTO applicationFile; //fixme need to remove

    @NotNull
    BusinessTypeEnum businessType;

    @NotNull
    AddressFullDTO address;

    @NotNull
    CtsBankDetailDTO bank;

    BigDecimal vatRate;

    Vat vat;

    @NotNull
    OwnerDTO owner;

    @NotNull
    List<QuickRegisterMerchantDto> branchList = new ArrayList<>();

    String tariffId;
}
