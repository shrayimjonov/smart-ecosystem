package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.OrganizationDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.invoice.FacturaCustomer;
import uz.uzkassa.silen.enumeration.AggregationMode;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.ProductGroup;
import uz.uzkassa.silen.integration.billing.enums.BillingDocumentType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * A Factory.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "organization",
        indexes = {
                @Index(name = "organization_tin_idx", columnList = "tin")
        })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE organization SET deleted = 'true' WHERE id=?")
public class Organization extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(length = 14, nullable = false)
    private String tin;

    private String name;

    private String account;

    private String address;

    private String mobile;

    private String workPhone;

    private String oked;

    private String director;

    private String accountant;

    @Column(name = "vat_reg_code")
    private String vatRegCode;

    @Column(name = "vat_reg_status")
    private Integer vatRegStatus;

    @Column(name = "tax_gap", precision = 25, scale = 2)
    private BigDecimal taxGap;

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(targetClass = OrganizationType.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "organization_types", joinColumns = @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)))
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<OrganizationType> types = new HashSet<>();

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "org_gcp", joinColumns = @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)))
    @Column(name = "gcp", nullable = false, length = 9)
    private Set<String> gcp = new HashSet<>();

    @Column(name = "district_id")
    private String districtId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", updatable = false, insertable = false)
    private District district;

    @Column(name = "bank_id")
    private String bankId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", updatable = false, insertable = false)
    private Bank bank;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_group")
    private ProductGroup productGroup;

    private String website;

    private String email;

    private Double lat;

    private Double lon;

    @Column(name = "turon_token")
    private String turonToken;

    @Column(name = "oms_id")
    private String omsId;

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "true_token")
    private String trueToken;

    @Column(name = "token_date")
    private LocalDateTime tokenDate;

    @OneToMany(mappedBy = "organization")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<OrganizationPermissions> permissions;

    @Column(name = "demo_allowed", columnDefinition = "boolean default false")
    private boolean demoAllowed = false;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "factory_id")
    private String factoryId;

    @Column(name = "factory_country")
    private String factoryCountry;

    @Column(name = "manager_id")
    private String managerId;

    @Column(name = "active", columnDefinition = "boolean default false")
    private boolean active = false;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", updatable = false, insertable = false)
    private User manager;

    @Column(name = "manager_assigned_at")
    private LocalDateTime manager_assigned_at;

    @Column(name = "trial_period")
    private LocalDate trialPeriod;

    @Column(name = "xfile_binded", columnDefinition = "boolean default false")
    private boolean xfileBinded = false;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "has_child", columnDefinition = "boolean default false")
    private boolean hasChild = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_document_type", columnDefinition = "varchar(15) default 'UNKNOWN'")
    private BillingDocumentType billingDocumentType = BillingDocumentType.UNKNOWN;

    @Column(name = "allow_foreign_codes", columnDefinition = "boolean default false")
    private boolean allowForeignCodes = false;

    @Column(name = "parent_date")
    private LocalDateTime parentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "aggregation_mode", columnDefinition = "varchar(10) default 'Single'")
    private AggregationMode aggregationMode = AggregationMode.Single;

    public Set<OrganizationType> getTypes() {
        if (types == null) {
            types = new HashSet<>();
        }
        return types;
    }

    public OrganizationDTO toDto() {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(getId());
        dto.setName(getName());
        dto.setTin(getTin());
        dto.setAddress(getAddress());
        dto.setMobile(getMobile());
        dto.setWorkPhone(getWorkPhone());
        dto.setAccount(getAccount());
        dto.setOked(getOked());
        dto.setDirector(getDirector());
        dto.setAccountant(getAccountant());
        dto.setWebsite(getWebsite());
        dto.setEmail(getEmail());
        dto.setLat(getLat());
        dto.setLon(getLon());
        dto.setAggregationMode(getAggregationMode());
        if (getAggregationMode() != null) {
            dto.setAggregationModeName(getAggregationMode().getText());
        }
        dto.setTuronToken(getTuronToken());
        dto.setOmsId(getOmsId());
        dto.setDemoAllowed(isDemoAllowed());
        dto.setContactPerson(getContactPerson());
        dto.setFactoryId(getFactoryId());
        dto.setFactoryCountry(getFactoryCountry());
        dto.setActive(isActive());
        dto.setProductGroup(getProductGroup());
//        dto.setTrueToken(getTrueToken());
        dto.setTrialPeriod(getTrialPeriod());
        dto.setHasChild(isHasChild());
        dto.setParentDate(getParentDate());
        dto.setManagerId(getManagerId());
        if (getProductGroup() != null) {
            dto.setProductGroupName(getProductGroup().getNameRu());
        }
        if (CollectionUtils.isNotEmpty(getTypes())) {
            dto.setTypes(EnumSet.copyOf(getTypes()));
        }
        dto.setDistrictId(getDistrictId());
        if (getDistrict() != null) {
            dto.setDistrictName(getDistrict().getNameLocale());
            dto.setRegionId(getDistrict().getRegionId());
            if (getDistrict().getRegion() != null) {
                dto.setRegionName(getDistrict().getRegion().getNameLocale());
            }
        }
        dto.setBankId(getBankId());
        if (getBank() != null) {
            dto.setBankName(getBank().getName());
        }
        dto.setXfileBinded(isXfileBinded());
        Hibernate.initialize(getGcp());
        dto.setGcp(new HashSet<>(getGcp()));

        return dto;
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(getId(), getName(), getTin());
    }

    public FacturaCustomer toFacturaCustomer() {
        FacturaCustomer dto = new FacturaCustomer();
        dto.setId(getId());
        dto.setTin(getTin());
        dto.setName(getName());
        dto.setAccount(getAccount());
        dto.setDistrictId(getDistrictId());
        if (getDistrict() != null) {
            dto.setRegionId(getDistrict().getRegionId());
            if (getDistrict().getRegion() != null) {
                dto.setRegionName(getDistrict().getRegion().getNameLocale());
            }
            dto.setDistrictName(getDistrict().getNameLocale());
        }
        dto.setBankId(getBankId());
        if (getBank() != null) {
            dto.setBankName(getBank().getName());
        }
        dto.setAddress(getAddress());
        dto.setMobile(getMobile());
        dto.setWorkPhone(getWorkPhone());
        dto.setOked(getOked());
        dto.setDistrictId(getDistrictId());
        dto.setDirector(getDirector());
        dto.setAccountant(getAccountant());
        dto.setVatRegCode(getVatRegCode());
        dto.setVatRegStatus(getVatRegStatus());
        if (CollectionUtils.isNotEmpty(getTypes())) {
            dto.setTypes(EnumSet.copyOf(getTypes()));
        }
        return dto;
    }
}



