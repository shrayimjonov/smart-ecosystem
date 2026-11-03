package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.customer.CustomerLookUpDTO;
import uz.uzkassa.cts.dto.customer.CustomerShortDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "customer", indexes = {
        @Index(columnList = "tin", name = "customer_tin_idx"),
        @Index(columnList = "name", name = "customer_name_idx"),
        @Index(columnList = "company_name", name = "customer_company_name_idx"),
        @Index(columnList = "tin, name, company_name", name = "search_keys_index"),
        @Index(columnList = "region_id", name = "customer_region_idx"),
        @Index(columnList = "district_id", name = "customer_district_idx"),
        @Index(columnList = "business_type", name = "customer_business_type_idx"),
        @Index(columnList = "activity_type_id", name = "customer_activity_type_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 3215005662L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "company_name")
    String companyName;

    @NotNull
    @NotEmpty
    @Size(min = 9, max = 14)
    @Column(name = "tin", unique = true)
    String tin;

    @Size(min = 9, max = 14)
    @Column(name = "old_tin", unique = true)
    String oldTin;

    @Size(min = 9, max = 14)
    @Column(name = "new_tin", unique = true)
    String newTin;

    @Column(name = "oked")
    String oked;

    @Column(name = "xfile_tin")
    String xfileTin;

    @NotNull
    @Column(name = "phone", nullable = false)
    String phone;

    @Embedded
    BaseAddress address;

    @Column(name = "activity_type_id")
    Long activityTypeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_type_id", updatable = false, insertable = false)
    ActivityType activityType;

    @Deprecated
    @Column(name = "parent_activity_type_id")
    Long parentActivityTypeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_activity_type_id", updatable = false, insertable = false)
    ActivityType parentActivityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type")
    BusinessType businessType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CustomerStatus status = CustomerStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "legal_status")
    CustomerLegalStatus legalStatus = CustomerLegalStatus.ACTIVE;

    @Column(name = "app_type")
    @Enumerated(EnumType.STRING)
    CustomerAppType appType = CustomerAppType.CTS;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "branch_id")
    Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @ManyToMany
    @JoinTable(
            name = "customer_branches",
            joinColumns = {@JoinColumn(name = "customer_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "branch_id", referencedColumnName = "id")})
    Set<Branch> branches = new HashSet<>();

    @Column(name = "customer_bank_id")
    Long customerBankId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_bank_id", updatable = false, insertable = false)
    CustomerBank customerBank;

    @Column(name = "vat_id")
    Long vatId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vat_id", updatable = false, insertable = false)
    Vat vat;

    @Column(name = "send_geolocation", columnDefinition = "boolean default false")
    boolean sendGeolocation = false;

    @Column(name = "allow_device_register", columnDefinition = "boolean default false")
    boolean allowDeviceRegister = false;

    @Column(name = "white_list", columnDefinition = "boolean default false")
    boolean whiteList = false;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = 'false'")
    List<CustomerBranch> customerBranches = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = 'false'")
    List<CustomerDevice> devices = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = 'false'")
    List<CustomerFiscalModule> fiscalModules = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = 'false'")
    List<OrderTariff> orderTariffs = new ArrayList<>();

    @Column(name = "apay_id")
    String apayId;

    @Column(name = "apay_tariff_id")
    String apayTariffId;

    @Column(name = "sync_date")
    LocalDateTime syncDate;

    @Column(name = "manager_id")
    Long managerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", updatable = false, insertable = false)
    User manager;

    String offerActivationCode;

    LocalDateTime offerActivationDate;

    @Column(name = "bank_tariff_id")
    Long bankTariffId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_tariff_id", insertable = false, updatable = false)
    BankTariff bankTariff;

    @Column(name = "payment_date")
    LocalDate paymentDate;

    public void setName(String name) {
        addChangeLog("name", this.name, name);
        this.name = name;
    }

    public void setCompanyName(String companyName) {
        addChangeLog("companyName", this.companyName, companyName);
        this.companyName = companyName;
    }

    public void setTin(String tin) {
        addChangeLog("tin", this.tin, tin);
        this.tin = tin;
    }

    public void setPhone(String phone) {
        addChangeLog("phone", this.phone, phone);
        this.phone = phone;
    }

    public void setActivityType(ActivityType activityType) {
        addChangeLog("activityType", this.activityType != null ? activityType.getName() : null, activityType != null ? activityType.getName() : null);
        this.activityType = activityType;
    }

    public void setBusinessType(BusinessType businessType) {
        addChangeLog("businessType", this.businessType != null ? businessType.getName() : null, businessType != null ? businessType.getName() : null);
        this.businessType = businessType;
    }

    public void setStatus(CustomerStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setCustomerBank(CustomerBank customerBank) {
        addChangeLog("customerBank", this.customerBank != null ? this.customerBank.getBank().getNameNotNullable() : null, customerBank != null ? customerBank.getBank().getNameNotNullable() : null);
        this.customerBank = customerBank;
    }

    public void setAllowDeviceRegister(boolean allowDeviceRegister) {
        addChangeLog("allowDeviceRegister", String.valueOf(this.allowDeviceRegister), String.valueOf(allowDeviceRegister));
        this.allowDeviceRegister = allowDeviceRegister;
    }

    public void setWhiteList(boolean whiteList) {
        addChangeLog("whiteList", String.valueOf(this.whiteList), String.valueOf(whiteList));
        this.whiteList = whiteList;
    }

    public void setLegalStatus(CustomerLegalStatus customerStatus) {
        addChangeLog("customerStatus", this.legalStatus != null ? this.legalStatus.getName() : null, customerStatus != null ? customerStatus.getName() : null);
        this.legalStatus = customerStatus;
    }

    public void setAppType(CustomerAppType appType) {
        addChangeLog("appType", this.appType != null ? this.appType.getNameRu() : null, appType != null ? appType.getNameRu() : null);
        this.appType = appType;
    }

    public void setManager(User manager) {
        addChangeLog("manager", this.manager != null ? this.manager.getFullName() : null, manager != null ? manager.getFullName() : null);
        this.manager = manager;
    }

    public void setBankTariff(BankTariff bankTariff) {
        addChangeLog("bankTariff", this.bankTariff != null ? this.bankTariff.getXizmatName() : null, bankTariff != null ? bankTariff.getXizmatName() : null);
        this.bankTariff = bankTariff;
    }

    public void setVat(Vat vat) {
        addChangeLog("vat", this.vat != null ? this.vat.getName() : null, vat != null ? vat.getName() : null);
        this.vat = vat;
    }

    public void setBranch(Branch branch) {
        addChangeLog("branch", this.branch != null ? this.branch.getName() : null, branch != null ? branch.getName() : null);
        this.branch = branch;
    }

    public void setBaseAddress(BaseAddress address) {
        addAddressChangeLog(this.address, address);
        this.address = address;
    }

    public CustomerLookUpDTO getAsLookUpDTO() {
        return new CustomerLookUpDTO(getId(), businessType.getName() + " " + getCompanyName(), getTin(), getAppType().getCode(), getVatId());
    }

    public boolean isTin() {
        return tin.length() == 9;
    }

    public CompanyCommonDTO toShortDTO() {
        return new CompanyCommonDTO(getId(), getName(), getTin());
    }

    public CustomerShortDTO toCustomerShortDTO() {
        return new CustomerShortDTO(getId(), getName(), getTin());
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public String getEntityName() {
        return this.name;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CUSTOMER;
    }
}
