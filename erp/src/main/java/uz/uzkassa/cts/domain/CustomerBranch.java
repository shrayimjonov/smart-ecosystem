package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.address.BaseAddress;
import uz.uzkassa.cts.domain.apay.APayCategory;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.device.CustomerBranchOfdDTO;
import uz.uzkassa.cts.enums.CustomerBranchStatus;
import uz.uzkassa.cts.enums.DeviceType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.OfdStatus;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_branch",
        indexes = {
                @Index(columnList = "name", name = "customer_branch_name_idx"),
                @Index(columnList = "customer_id", name = "customer_branch_customer_id_idx"),
                @Index(columnList = "activity_type_id", name = "customer_branch_activity_type_id_idx"),
                @Index(columnList = "branch_id", name = "customer_branch_branch_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE customer_branch SET deleted = 'true' WHERE id=?")
public class CustomerBranch extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 1687188649L;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "brand")
    String brand;

    @Column(name = "activity_type_id")
    Long activityTypeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_type_id", updatable = false, insertable = false)
    ActivityType activityType;

    @Column(name = "apay_category_id")
    Long aPayCategoryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apay_category_id", updatable = false, insertable = false)
    APayCategory aPayCategory;

    @Column(name = "phone")
    String phone;

    @Column(name = "cadastre_number")
    String cadastreNumber;

    @Column(name = "apay_id")
    Long apayId;

    @Column(name = "ofd_id")
    Long ofdId;

    @Column(name = "ofd_status")
    @Enumerated(EnumType.STRING)
    OfdStatus ofdStatus;

    @Column(name = "resync", columnDefinition = "boolean default false")
    boolean resync = false;

    @Column(name = "has_act", columnDefinition = "boolean default false")
    boolean hasAct = false;

    @Column(name = "has_invoice_payment", columnDefinition = "boolean default false")
    boolean hasInvoicePayment = false;

    @Embedded
    BaseAddress address;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CustomerBranchStatus status = CustomerBranchStatus.PENDING;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "reason")
    String reason;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @Column(name = "branch_id")
    Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "sync_date")
    LocalDateTime syncDate;

    @Column(name = "scanner_file_uri")
    String scannerFileUri;

    @Column(name = "scanner_file_name")
    String scannerFileName;

    @Column(name = "scanner_id")
    Long scannerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scanner_id", updatable = false, insertable = false)
    CustomerBranchScanner customerBranchScanner;

    @Column(name = "device_type")
    @Enumerated(EnumType.STRING)
    DeviceType deviceType = DeviceType.FISCAL;

    @OneToMany(mappedBy = "customerBranch", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = 'false' and status!='TRANSFERRED'")
    List<CustomerBranchDevice> devices = new ArrayList<>();

    @OneToMany(mappedBy = "customerBranch", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "deleted = 'false' and status!='TRANSFERRED'")
    List<CustomerBranchFiscalModule> fiscalModules = new ArrayList<>();

    @OneToMany(mappedBy = "customerBranch", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "active = 'true'")
    List<CustomerBranchRent> customerBranchRents = new ArrayList<>();

    @OneToMany(mappedBy = "customerBranch", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction(value = "active = 'true'")
    List<CustomerBranchRent> customerBranchRents = new ArrayList<>();

    public CustomerBranchOfdDTO getDTO() {
        CustomerBranchOfdDTO dto = new CustomerBranchOfdDTO();
        dto.setId(getId());
        dto.setName(getName());
        dto.setOfdId(getOfdId());
        if (getCustomerBranchScanner() != null) {
            dto.setScannerFileName(getScannerFileName());
            dto.setScannerFileUri(getScannerFileUri());
            dto.setScannerCode(Integer.valueOf(getCustomerBranchScanner().getCode()));
            dto.setScannerName(getCustomerBranchScanner().getName());
        }
        return dto;
    }

    public void setName(String name) {
        addChangeLog("name", this.name, name);
        this.name = name;
    }

    public void setPhone(String phone) {
        addChangeLog("phone", this.phone, phone);
        this.phone = phone;
    }

    public void setCadastreNumber(String cadastreNumber) {
        addChangeLog("cadastreNumber", this.cadastreNumber, cadastreNumber);
        this.cadastreNumber = cadastreNumber;
    }

    public void setHasAct(boolean hasAct) {
        addChangeLog("hasAct", String.valueOf(this.hasAct), String.valueOf(hasAct));
        this.hasAct = hasAct;
    }

    public void setHasInvoicePayment(boolean hasInvoicePayment) {
        addChangeLog("hasInvoicePayment", String.valueOf(this.hasInvoicePayment), String.valueOf(hasInvoicePayment));
        this.hasInvoicePayment = hasInvoicePayment;
    }

    public void setOfdStatus(OfdStatus ofdStatus) {
        addChangeLog("ofdStatus", this.ofdStatus != null ? this.ofdStatus.getName() : null, ofdStatus != null ? ofdStatus.getName() : null);
        this.ofdStatus = ofdStatus;
    }

    public void setStatus(CustomerBranchStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setCustomerBranchScanner(CustomerBranchScanner customerBranchScanner) {
        addChangeLog("scanner", this.customerBranchScanner != null ? this.customerBranchScanner.getName() : null, customerBranchScanner != null ? customerBranchScanner.getName() : null);
        this.customerBranchScanner = customerBranchScanner;
    }

    public void setActivityType(ActivityType activityType) {
        addChangeLog("activityType", this.activityType != null ? activityType.getName() : null, activityType != null ? activityType.getName() : null);
        this.activityType = activityType;
    }

    public void setAPayCategory(APayCategory aPayCategory) {
        addChangeLog("aPayCategory", this.aPayCategory != null ? aPayCategory.getName() : null, aPayCategory != null ? aPayCategory.getName() : null);
        this.aPayCategory = aPayCategory;
    }

    public void setDeviceType(DeviceType deviceType) {
        addChangeLog("type", this.deviceType != null ? deviceType.getName() : null, deviceType != null ? deviceType.getName() : null);
        this.deviceType = deviceType;
    }

    public void setBaseAddress(BaseAddress address) {
        addAddressChangeLog(this.address, address);
        this.address = address;
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
        return EntityTypeEnum.CUSTOMER_BRANCH;
    }
}
