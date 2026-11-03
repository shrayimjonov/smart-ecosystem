package uz.uzkassa.smartpos.domain.postgres.company;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.base.SimpleEntity;
import uz.uzkassa.smartpos.domain.postgres.common.Bank;
import uz.uzkassa.smartpos.domain.postgres.common.City;
import uz.uzkassa.smartpos.domain.postgres.common.Region;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.CompanyDTO;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.enums.*;
import uz.uzkassa.smartpos.integration.apay.AddressFullDTO;
import uz.uzkassa.smartpos.integration.apay.CtsBankDetailDTO;
import uz.uzkassa.smartpos.integration.apay.FileDTO;
import uz.uzkassa.smartpos.integration.apay.RegionDistrictDTO;
import uz.uzkassa.smartpos.integration.apay.request.CompanyRegisterDTO;
import uz.uzkassa.smartpos.integration.enums.BusinessTypeEnum;
import uz.uzkassa.smartpos.integration.enums.MerchantReferenceType;
import uz.uzkassa.smartpos.utils.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "company", uniqueConstraints = {
        @UniqueConstraint(columnNames = "inn", name = "company_inn_un_idx")
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Company extends SimpleEntity {
    @Column(name = "cto_id")
    Long ctoId;

    @Column(name = "logo_id")
    Long logoId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    FileUpload logo;

    @NotNull
    @Column(name = "inn", columnDefinition = "varchar(14)")
    String inn;
    @Column(name = "old_inn", columnDefinition = "varchar(14)")
    String oldInn;

    @NotNull
    @Column(name = "name")
    String name;

    @Column(name = "brand")
    String brand;

    @Column(name = "oked", columnDefinition = "varchar(50)")
    String oked;

    @Column(name = "mfo", columnDefinition = "varchar(50)")
    String mfo;

    @Column(name = "account_number", columnDefinition = "varchar(50)")
    String accountNumber;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "mfo", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    Bank bank;

    @Size(max = 500)
    @Column(name = "address", length = 500)
    String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type")
    BusinessType businessType;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type")
    CompanyType companyType;

    @Setter
    @Column(name = "nds_percent", columnDefinition = "numeric(5,2)")
    BigDecimal ndsPercent;

    @Column(columnDefinition = "boolean default false")
    boolean binded;

    @Setter
    @Column(name = "fiscal", columnDefinition = "boolean default false")
    boolean fiscal;

    @Setter
    @Column(name = "sync_date")
    LocalDateTime syncDate;

    @Column(name = "region_id")
    Long regionId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    Region region;

    @Column(name = "city_id")
    Long cityId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    City city;

    @Setter
    @Column(name = "owner_id")
    Long ownerId;
    @Setter
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", updatable = false, insertable = false)
    @ToString.Exclude
    User owner;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = 'false'")
    @ToString.Exclude
    List<Branch> branches = new ArrayList<>();

    @Setter
    @ManyToMany
    @JoinTable(
            name = "company_activity_type",
            joinColumns = {@JoinColumn(name = "company_id")},
            inverseJoinColumns = {@JoinColumn(name = "activity_type_id")}
    )
    @ToString.Exclude
    Set<ActivityType> activityTypes = new HashSet<>();

    @Setter
    @ElementCollection(targetClass = PaymentType.class)
    @CollectionTable(name = "company_payment_types", joinColumns = @JoinColumn(name = "company_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)))
    @Column(name = "payment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    Set<PaymentType> paymentTypes = new HashSet<>();

    //Apay
    @Column(name = "apay_id")
    String apayId;
    @Column(name = "cadastre_file_id")
    String cadastreFileId;
    @Column(name = "application_file_id")
    String applicationFileId;
    @Column(name = "ofd_file_id")
    String ofdFileId;
    @Column(name = "has_certificate", columnDefinition = "boolean default false")
    boolean hasCertificate = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status")
    ApplicationStatus applicationStatus;
    @Column(name = "fiscal_number")
    String fiscalNumber;
    @Column(name = "free_trial", columnDefinition = "boolean default false")
    private boolean freeTrial;
    @Column(name = "trial_date")
    private LocalDateTime trialDate;

    @OneToMany(mappedBy = "company")
    private Set<CompanyPermission> companyPermissions = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "company_balances",
            joinColumns = {@JoinColumn(name = "company_id", referencedColumnName = "id")})
    @Column(name = "balance")
    private Map<String, Integer> balances = new HashMap<>();

    @Column(name = "manager_id")
    Long managerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", updatable = false, insertable = false)
    @ToString.Exclude
    User manager;
    @Column(name = "support_id")
    Long supportId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_id", updatable = false, insertable = false)
    @ToString.Exclude
    User support;
    @Column(name = "admin", columnDefinition = "boolean default false")
    private boolean admin;
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_type", columnDefinition = "varchar(50)")
    private BillingType billingType;
    @Column(name = "apay_tariff_id", columnDefinition = "varchar(50)")
    private String apayTariffId;
    @Column(name = "reservation_enabled", columnDefinition = "boolean default false")
    private boolean reservationEnabled;

    public Map<String, LocalDateTime> getPermissions() {
        if (CollectionUtils.isEmpty(companyPermissions)) {
            return new HashMap<>();
        }
        return companyPermissions.stream()
                .collect(Collectors.toMap(CompanyPermission::getServiceType, CompanyPermission::getEndDate, (a, b) -> a));
    }

    public void setFreeTrial(boolean freeTrial) {
        if (this.freeTrial == !freeTrial) {
            addLog(AuditedFields.FREE_TRIAL, this.freeTrial ? "Да" : "Нет", freeTrial ? "Да" : "Нет", getId());
        }
        this.freeTrial = freeTrial;
    }

    public void setTrialDate(LocalDateTime trialDate) {
        if (!Objects.equals(this.trialDate, trialDate)) {
            addLog(AuditedFields.FREE_TRIAL_DATE, Objects.nonNull(this.trialDate) ? DateUtils.format(this.trialDate) : "", Objects.nonNull(trialDate) ? DateUtils.format(trialDate) : "", getId());
        }
        this.trialDate = trialDate;
    }

    public void setInn(String inn) {
        if (!Objects.equals(this.inn, inn)) {
            addLog(AuditedFields.TIN, this.inn, inn, getId());
        }
        this.inn = inn;
    }

    public void setName(String name) {
        if (!Objects.equals(this.name, name)) {
            addLog(AuditedFields.NAME, this.name, name, getId());
        }
        this.name = name;
    }

    public void setMfo(String mfo) {
        this.mfo = StringUtils.trimToNull(mfo);
    }

    public void setAddress(String address) {
        if (!Objects.equals(this.address, address)) {
            addLog(AuditedFields.ADDRESS, this.address, address, getId());
        }
        this.address = address;
    }

    public void setBusinessType(BusinessType businessType) {
        if (!Objects.equals(this.businessType, businessType)) {
            addLog(AuditedFields.BUSINESS_TYPE, this.businessType != null ? this.businessType.getNameRu() : "", businessType != null ? businessType.getNameRu() : "", getId());
        }
        this.businessType = businessType;
    }

    public void setRegionId(Long regionId) {
        if (!Objects.equals(this.regionId, regionId)) {
            addLog(AuditedFields.REGION, this.region != null ? this.region.getNameRu() : "", region != null ? region.getNameRu() : "", getId());
        }
        this.regionId = regionId;
    }

    public void setCityId(Long cityId) {
        if (!Objects.equals(this.cityId, cityId)) {
            addLog(AuditedFields.DISTRICT, this.city != null ? this.city.getNameRu() : "", city != null ? city.getNameRu() : "", getId());
        }
        this.cityId = cityId;
    }

    public Set<ActivityType> getActivityTypes() {
        if (activityTypes == null) {
            this.activityTypes = new HashSet<>();
        }
        return activityTypes;
    }

    public Set<PaymentType> getPaymentTypes() {
        if (paymentTypes == null) {
            paymentTypes = new HashSet<>();
        }
        return paymentTypes;
    }

    public void updateBalance(String serviceType, Integer amount) {
        if (balances == null) {
            balances = new HashMap<>();
        }
        balances.put(serviceType, amount + balances.getOrDefault(serviceType, 0));
    }

    public CompanyDTO getDTO() {
        CompanyDTO companyDTO = setAuditValues(new CompanyDTO());

        companyDTO.setId(getId());
        companyDTO.setCtoId(getCtoId());
        companyDTO.setInn(getInn());
        companyDTO.setName(getName());
        companyDTO.setBrand(getBrand());
        companyDTO.setOked(getOked());
        companyDTO.setAccountNumber(getAccountNumber());
        companyDTO.setMfo(getMfo());
        if (getBank() != null) {
            companyDTO.setBankName(getBank().getName());
        }
        companyDTO.setAddress(getAddress());
        companyDTO.setFiscal(isFiscal());
        companyDTO.setBusinessType(getBusinessType());
        companyDTO.setCompanyType(getCompanyType());
        companyDTO.setPaysNds(getNdsPercent() != null);
        companyDTO.setNdsPercent(getNdsPercent());
        companyDTO.setWarehouseEnabled(true);
        companyDTO.setSyncDate(getSyncDate());
        companyDTO.setOwnerId(getOwnerId());
        companyDTO.setReservationEnabled(isReservationEnabled());
        if (getOwner() != null) {
            companyDTO.setContactFullName(getOwner().getFullName());
            companyDTO.setPhone(getOwner().getLogin());
        }
        if (getRegion() != null) {
            companyDTO.setRegion(getRegion().getDTO());
        }
        if (getCity() != null) {
            companyDTO.setCity(getCity().getDTO());
        }
        if (CollectionUtils.isNotEmpty(getActivityTypes())) {
            companyDTO.setTypes(getActivityTypes().stream().map(ActivityType::getDTO).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(getPaymentTypes())) {
            companyDTO.setPaymentTypes(getPaymentTypes());
        }
        companyDTO.setAutoPrintEnabled(false);
        companyDTO.setCadastreFileId(getCadastreFileId());
        companyDTO.setApplicationFileId(getApplicationFileId());
        companyDTO.setApplicationStatus(getApplicationStatus());
        companyDTO.setPermissions(getPermissions().entrySet().stream().filter(entry -> entry.getValue() != null && entry.getValue().isAfter(LocalDateTime.now())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        companyDTO.setBalances(getBalances());
        companyDTO.setFreeTrial(isFreeTrial());
        companyDTO.setTrialDate(getTrialDate());
        if (getLogo() != null) {
            companyDTO.setLogoUrl(getLogo().getUrl());
        }
        if (getManager() != null) {
            companyDTO.setManagerLogin(getManager().getLogin());
            companyDTO.setManagerLastName(getManager().getLastName());
            companyDTO.setManagerFirstName(getManager().getFirstName());
            companyDTO.setManagerPatronymic(getManager().getPatronymic());
        }
        companyDTO.setApayTariffId(getApayTariffId());
        companyDTO.setSupportId(getSupportId());
        if (getSupport() != null) {
            companyDTO.setSupportName(getSupport().getName());
        }
        companyDTO.setBillingType(getBillingType());
        return companyDTO;
    }

    public CompanyRegisterDTO toApayDTO() {
        CompanyRegisterDTO dto = new CompanyRegisterDTO();
        dto.setName(getName());
        dto.setBrandName(getBrand());
        dto.setTin(getInn());
        if (getOwner() != null) {
            dto.setPhone(getOwner().getLogin());
            dto.setOwner(getOwner().toOwnerDTO());
        }
        dto.setReferenceType(MerchantReferenceType.SMARTPOS);
        if (CollectionUtils.isNotEmpty(getActivityTypes())) {
            dto.setActivityType(getActivityTypes().stream().filter(activityType -> activityType.getParentId() != null).findFirst().map(ActivityType::toApayDTO).orElseThrow(() -> new NoSuchElementException("Activity type")));
        }
        dto.setCadastreRentFile(new FileDTO(getCadastreFileId()));
        dto.setApplicationFile(new FileDTO(getApplicationFileId()));
        if (getBusinessType() != null) {
            dto.setBusinessType(BusinessTypeEnum.valueOf(getBusinessType().name()));
        }
        dto.setAddress(getAddressDTO());
        dto.setBank(getBankDTO());
        dto.setVatRate(getNdsPercent());
        dto.setTariffId(getApayTariffId());
        return dto;
    }

    public AddressFullDTO getAddressDTO() {
        AddressFullDTO dto = new AddressFullDTO();
        if (getRegion() != null && getRegion().getCode() != null) {
            dto.setRegion(new RegionDistrictDTO(getRegion().getCode().toString()));
        }
        if (getCity() != null && getCity().getCode() != null) {
            dto.setDistrict(new RegionDistrictDTO(getCity().getCode().toString()));
        }
        dto.setStreet(getAddress());
        return dto;
    }

    public CtsBankDetailDTO getBankDTO() {
        CtsBankDetailDTO dto = new CtsBankDetailDTO();
        dto.setAccountNumber(getAccountNumber());
        dto.setMfo(getMfo());
        if (getBank() != null) {
            dto.setName(getBank().getName());
        }
        dto.setOked(getOked());
        dto.setTin(getInn());
        return dto;
    }

    @Override
    public EntityType getHistoryType() {
        return EntityType.COMPANY;
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(this.getId(), this.getName());
    }

    public CompanyBaseDTO toCompanyBaseDTO() {
        return new CompanyBaseDTO(getId(), getName(), getInn());
    }

}
