package uz.uzkassa.smartpossupply.domain.company;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.domain.ActivityType;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.billing.UsagePlan;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.company.RevenueByRegionDTO;
import uz.uzkassa.smartpossupply.dto.company.TopClientDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerStatisticItemDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "TopClientMapper",
                        classes = @ConstructorResult(
                                targetClass = TopClientDTO.class,
                                columns = {
                                        @ColumnResult(name = "percentage", type = BigDecimal.class),
                                        @ColumnResult(name = "amount", type = BigDecimal.class),
                                        @ColumnResult(name = "companyId", type = Long.class),
                                        @ColumnResult(name = "companyName", type = String.class),
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "RevenueByRegionMapper",
                        classes = @ConstructorResult(
                                targetClass = RevenueByRegionDTO.class,
                                columns = {
                                        @ColumnResult(name = "regionId", type = Long.class),
                                        @ColumnResult(name = "amount", type = BigDecimal.class),
                                        @ColumnResult(name = "name", type = String.class),
                                        @ColumnResult(name = "chartCode", type = String.class),
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "CustomerStatisticItemMapper",
                        classes = @ConstructorResult(
                                targetClass = CustomerStatisticItemDTO.class,
                                columns = {@ColumnResult(name = "dataType", type = String.class), @ColumnResult(name = "total", type = Long.class)}
                        )
                ),
        }
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "company",
        indexes = {
                @Index(columnList = "tin", name = "company_tin_idx"),
                @Index(columnList = "name", name = "company_name_idx"),
                @Index(columnList = "tin, name", name = "company_search_keys_index"),
        }
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE company SET deleted = true WHERE id=?")
//@SQLRestriction(value = "deleted = false")
public class Company extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 9L;

    @Column(name = "name")
    String name;

    @Column(name = "brand")
    String brand;

    @Column(name = "erp_id", unique = true)
    Long erpId;

    @Column(name = "tin", length = 14)
    @Pattern(regexp = Constants.TIN_REGEX)
    String tin;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type")
    BusinessType businessType;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type")
    CompanyType companyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_group")
    ProductGroup productGroup;

    @Column(name = "vat")
    @Enumerated(value = EnumType.STRING)
    Vat vat;

    @Embedded
    Address address;

    @Column(name = "owner_id")
    Long ownerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", updatable = false, insertable = false)
    User owner;

    @Column(name = "logo_id")
    Long logoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    FileUpload logo;

    @Column(name = "background_image_id")
    Long backgroundImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "background_image_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    FileUpload backgroundImage;

    @Column(name = "activity_type_id")
    Long activityTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_type_id", updatable = false, insertable = false)
    ActivityType activityType;

    @Column(name = "company_bank_id")
    Long companyBankId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_bank_id", updatable = false, insertable = false)
    CompanyBank companyBank;

    @Column(name = "free_trial", columnDefinition = "boolean default false")
    boolean freeTrial = false;

    @Column(name = "trial_expire_date")
    LocalDateTime trialExpireDate;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    Set<UsagePlan> usagePlans = new HashSet<>();

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, orphanRemoval = true, cascade =  CascadeType.ALL)
    Set<CompanyPermission> permissions = new HashSet<>();

    @Column(name = "phone")
    String phone;

    @Column(name = "contact_person")
    String contactPerson;

    @Column(name = "director")
    String director;

    @Column(name = "accountant")
    String accountant;

    @Column(name = "has_financial_discount", columnDefinition = "boolean default false")
    boolean hasFinancialDiscount = false;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CompanyStatus status;

    @Column(name = "registration_date")
    LocalDateTime registrationDate;

    @Column(name = "allow_invoice_sync", columnDefinition = "boolean default false")
    boolean allowInvoiceSync = false;

    @Column(name = "allow_catalog_sync", columnDefinition = "boolean default false")
    boolean allowCatalogSync = false;

    @ElementCollection(targetClass = PaymentType.class)
    @CollectionTable(
            name = "company_payment_types",
            joinColumns = @JoinColumn(name = "company_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    )
    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    Set<PaymentType> paymentTypes = new HashSet<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<Branch> branches = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<CustomerBranch> customerBranches = new ArrayList<>();

    @Column(name = "manager_id")
    Long managerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", updatable = false, insertable = false)
    User manager;

    @Column(name = "manager_assigned_date")
    LocalDateTime managerAssignedDate;

    @Column(name = "branch_count", columnDefinition = "int default 0")
    int branchCount;

    @Column(name = "customer_branch_count", columnDefinition = "int default 0")
    int customerBranchCount;

    @Column(name = "equipment_count", columnDefinition = "int default 0")
    int equipmentCount;

    @Column(name = "has_contract", columnDefinition = "boolean default false")
    boolean hasContract;

    @Column(name = "x_file_enabled", columnDefinition = "boolean default false")
    boolean xFileEnabled;

    @Column(name = "one_c_enabled", columnDefinition = "boolean default false")
    boolean oneCEnabled;

    @Column(name = "marketplace_id")
    Long marketplaceId;

    @Column(name = "marketplace_settings_step")
    @Enumerated(EnumType.STRING)
    MarketplaceSettingsStep marketplaceSettingsStep;

    @Column(name = "telegram_language_code")
    String telegramLanguageCode;

    @Column(name = "telegram_user_id")
    Long telegramUserId;

    @Column(name = "telegram_chat_id")
    Long telegramChatId;

    @Column(name = "mobile_id", unique = true)
    String mobileId;

    @Column(name = "parent_id")
    Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    Company parent;

    @Column(name = "patent_date")
    LocalDateTime patentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    SourceType source;

    public void addUsagePLan(UsagePlan usagePlan) {
        usagePlan.setCompany(this);
        usagePlans.add(usagePlan);
    }

    public void addPermission(CompanyPermission permission) {
        permission.setCompany(this);
        permissions.add(permission);
    }

    public void addPaymentType(PaymentType paymentType) {
        getPaymentTypes().add(paymentType);
    }

    public void removePaymentType(PaymentType paymentType) {
        getPaymentTypes().remove(paymentType);
    }

    public CompanyBaseDTO toCommonBaseDTO() {
        return new CompanyBaseDTO(getId(), getName(), getTin(), getBrand());
    }


    public void setName(String name) {
        addLogHistory("name", this.name, name);
        this.name = name;
    }

    public void setBrand(String brand) {
        addLogHistory("brand", this.brand, brand);
        this.brand = brand;
    }

    public void setTin(String tin) {
        addLogHistory("tin", this.tin, tin);
        this.tin = tin;
    }

    public void setBusinessType(BusinessType businessType) {
        addLogHistory("businessType", this.businessType != null ? this.businessType.getName() : null, businessType != null ? businessType.getName() : null);
        this.businessType = businessType;
    }

    public void setCompanyType(CompanyType companyType) {
        addLogHistory("companyType", this.companyType != null ? this.companyType.getName() : null, companyType != null ? companyType.getName() : null);
        this.companyType = companyType;
    }

    public void setAddress(Address address) {
        addAddressLogHistory(this.address, address);
        this.address = address;
    }

    public void setPhone(String phone) {
        addLogHistory("phone", this.phone, phone);
        this.phone = phone;
    }

    public void setContactPerson(String contactPerson) {
        addLogHistory("contactPerson", this.contactPerson, contactPerson);
        this.contactPerson = contactPerson;
    }

    public void setStatus(CompanyStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setManager(User manager) {
        if (!Objects.equals(this.manager, manager)) {
            addLogHistory("manager", this.manager != null ? this.manager.getFullName() : null, manager != null ? manager.getFullName() : null);
        }
        this.manager = manager;
    }

    public void setActivityType(ActivityType activityType) {
        if (!Objects.equals(this.activityType, activityType)) {
            addLogHistory("activityType", this.activityType != null ? this.activityType.getName() : null, activityType != null ? activityType.getName() : null);
        }
        this.activityType = activityType;
    }

    public void setProductGroup(ProductGroup productGroup) {
        addLogHistory("productGroup", this.productGroup != null ? this.productGroup.getName() : null, productGroup != null ? productGroup.getName() : null);
        this.productGroup = productGroup;
    }

    public void setFreeTrial(boolean freeTrial) {
        addLogHistory("freeTrial", this.freeTrial, freeTrial);
        this.freeTrial = freeTrial;
    }

    public void setDirector(String director) {
        addLogHistory("director", this.director, director);
        this.director = director;
    }

    public void setAccountant(String accountant) {
        addLogHistory("accountant", this.accountant, accountant);
        this.accountant = accountant;
    }

    public void setHasFinancialDiscount(boolean hasFinancialDiscount) {
        addLogHistory("hasFinancialDiscount", this.hasFinancialDiscount, hasFinancialDiscount);
        this.hasFinancialDiscount = hasFinancialDiscount;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.COMPANY;
    }

    @Override
    public String getEntityName() {
        return this.name;
    }
}
