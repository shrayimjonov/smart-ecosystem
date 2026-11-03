package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.enums.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ToString
public class CompanyDTO extends BaseAuditDTO implements Serializable {
    @NotNull
    private FullNameDTO contactFullName;
    private String phone;
    private Long ctoId;
    @Size(max = 50)
    private String inn;
    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    private String brand;
    private String oked;
    private String accountNumber;
    private String mfo;
    private String bankId;
    private String bankName;
    private ApplicationStatus applicationStatus;
    @NotNull
    private BusinessType businessType;
    private CompanyType companyType;
    private String address;
    private RegionDTO region;
    private CityDTO city;
    private List<ActivityTypeDTO> types;
    private boolean paysNds;
    private BigDecimal ndsPercent;
    private boolean warehouseEnabled;
    private boolean autoPrintEnabled;
    private boolean fiscal;
    private LocalDateTime syncDate;
    private Set<PaymentType> paymentTypes = new HashSet<>();
    private String smsCode;
    private String trueToken;
    private LocalDateTime tokenDate;
    private String apayId;
    private String cadastreFileId;
    private String applicationFileId;
    private Map<String, Integer> balances;
    private Map<String, LocalDateTime> permissions;
    private boolean freeTrial;
    private LocalDateTime trialDate;
    private String logoUrl;
    private String managerLogin;
    private String managerFirstName;
    private String managerLastName;
    private String managerPatronymic;
    private Long ownerId;
    private String apayTariffId;
    private Long supportId;
    private String supportName;
    private boolean reservationEnabled;
    private BillingType billingType;

}
