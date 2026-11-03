package uz.uzkassa.cts.dto.apay;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.file.FileDTO;
import uz.uzkassa.cts.enums.BusinessType;
import uz.uzkassa.cts.enums.apay.APayMerchantReferenceType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class APayCompanyRegisterDTO implements Serializable {
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
    APayMerchantReferenceType referenceType;

    @NotNull
    ApayActivityTypeDTO activityType;

    @NotNull
    BusinessType businessType;

    @NotNull
    ApayAddressDTO address;

    @NotNull
    ApayBankDTO bank;

    BigDecimal vatRate;

    @NotNull
    DirectorDTO owner;

    FileDTO cadastreRentFile = new FileDTO(); //need to remove when apay ready
    FileDTO applicationFile = new FileDTO(); //need to remove when apay ready

    @NotNull
    List<APayRegisterMerchantDTO> branchList = new ArrayList<>();

    String tariffId;

    public APayCompanyRegisterDTO(Customer customer) {
        this.name = customer.getName();
        this.tin = customer.getTin();
        this.phone = customer.getPhone();
        this.referenceType = APayMerchantReferenceType.CTO;
        this.activityType = new ApayActivityTypeDTO(customer.getActivityType());
        this.businessType = customer.getBusinessType();
        this.address = new ApayAddressDTO(customer.getAddress());
        this.bank = new ApayBankDTO(customer.getCustomerBank());
        if (customer.getVat() != null) {
            this.vatRate = customer.getVat().getPercent();
        }
        this.tariffId = customer.getApayTariffId();
    }
}
