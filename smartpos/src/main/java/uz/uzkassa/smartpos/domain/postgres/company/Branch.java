package uz.uzkassa.smartpos.domain.postgres.company;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.base.SimpleEntity;
import uz.uzkassa.smartpos.domain.postgres.common.City;
import uz.uzkassa.smartpos.domain.postgres.common.Neighborhood;
import uz.uzkassa.smartpos.domain.postgres.common.Region;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.dto.application.OfdStatusEnum;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.enums.ApayRegisterStatus;
import uz.uzkassa.smartpos.enums.AuditedFields;
import uz.uzkassa.smartpos.enums.EntityType;
import uz.uzkassa.smartpos.integration.apay.AddressFullDTO;
import uz.uzkassa.smartpos.integration.apay.QuickRegisterMerchantDto;
import uz.uzkassa.smartpos.integration.apay.RegionDistrictDTO;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Filial
 */
@Entity
@Table(name = "branch",
        indexes = {
                @Index(columnList = "name", name = "branch_name_idx"),
                @Index(columnList = "group_id", name = "branch_group_idx"),
                @Index(columnList = "company_id", name = "branch_company_idx")
        }
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
public class Branch extends SimpleEntity {
    @Column(name = "cto_id", unique = true)
    private Long ctoId;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "latitude")
    private String latitude;
    @Size(max = 500)
    @Column(name = "address", length = 500)
    private String address;
    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;
    @Column(name = "deleted", columnDefinition = "boolean default false")
    private boolean deleted = false;
    @Setter
    @Column(name = "phone")
    private String phone;
    @Column(name = "fiscal", columnDefinition = "boolean default false")
    private boolean fiscal;

    @Column(name = "region_id")
    private Long regionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Region region;

    @Column(name = "city_key_id")
    private Long cityKeyId;
    @Column(name = "city_id")
    private Long cityId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private City city;

    @Column(name = "neighborhood_id")
    private Long neighborhoodId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "neighborhood_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Neighborhood neighborhood;

    @NotNull
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "branches")
    private Set<BranchGroup> groups = new HashSet<>();

    @Column(name = "activity_type_id")
    private Long activityTypeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_type_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ActivityType activityType;

    @OneToMany(mappedBy = "branch")
    private Set<User> users;

    @OneToMany(mappedBy = "branch")
    private Set<Device> devices;

    @Column(name = "serial_number", unique = true)
    private String serialNumber;
    @Column(name = "cashbox_created_date")
    private LocalDateTime cashboxCreatedDate;

    @Column(name = "ofd_id")
    private Long ofdId;

    @Column(name = "ofd_status")
    @Enumerated(EnumType.STRING)
    private OfdStatusEnum ofdStatus;

    @Column(name = "rent_file_id")
    private Long rentFileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rent_file_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private FileUpload rentFile;

    @Column(name = "cadastre_file_id")
    private Long cadastreFileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cadastre_file_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private FileUpload cadastreFile;

    @Column(name = "manager_id")
    private Long managerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", updatable = false, insertable = false)
    private User manager;
    @Column(name = "main_branch")
    private boolean mainBranch;
    @Enumerated(EnumType.STRING)
    @Column(name = "apay_status")
    private ApayRegisterStatus apayStatus;

    public void setCashboxCreatedDate(LocalDateTime cashboxCreatedDate) {
        if (!Objects.equals(this.cashboxCreatedDate, cashboxCreatedDate)) {
            addLog(AuditedFields.WEBCASSA, this.cashboxCreatedDate, cashboxCreatedDate, getCompanyId());
        }
        this.cashboxCreatedDate = cashboxCreatedDate;
    }

    public void setSerialNumber(String serialNumber) {
        if (!Objects.equals(this.serialNumber, serialNumber)) {
            addLog(AuditedFields.SERIAL_NUMBER, this.serialNumber, serialNumber, getCompanyId());
        }
        this.serialNumber = serialNumber;
    }

    public void setName(String name) {
        if (!Objects.equals(this.name, name)) {
            addLog(AuditedFields.NAME, this.name, name, getCompanyId());
        }
        this.name = name;
    }

    public void setLongitude(String longitude) {
        if (!Objects.equals(this.longitude, longitude)) {
            addLog(AuditedFields.LONGITUDE, this.longitude, longitude, getCompanyId());
        }
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        if (!Objects.equals(this.latitude, latitude)) {
            addLog(AuditedFields.LATITUDE, this.latitude, latitude, getCompanyId());
        }
        this.latitude = latitude;
    }

    public void setAddress(String address) {
        if (!Objects.equals(this.address, address)) {
            addLog(AuditedFields.ADDRESS, this.address, address, getCompanyId());
        }
        this.address = address;
    }

    public void setRegionId(Long regionId) {
        if (!Objects.equals(this.regionId, regionId)) {
            addLog(AuditedFields.REGION, this.region != null ? this.region.getNameRu() : "", region != null ? region.getNameRu() : "", getCompanyId());
        }
        this.regionId = regionId;
    }

    public void setCityId(Long cityId) {
        if (!Objects.equals(this.cityId, cityId)) {
            addLog(AuditedFields.DISTRICT, this.city != null ? this.city.getNameRu() : "", city != null ? city.getNameRu() : "", getCompanyId());
        }
        this.cityId = cityId;
        if (this.cityKeyId == null) {
            this.cityKeyId = cityId;
        }
    }

    public void setActivityTypeId(Long activityTypeId) {
        if (!Objects.equals(this.activityTypeId, activityTypeId)) {
            addLog(AuditedFields.ACTIVITY_TYPE, this.activityType != null ? this.activityType.getNameRu() : "", activityType != null ? activityType.getNameRu() : "", getCompanyId());
        }
        this.activityTypeId = activityTypeId;
    }

    public QuickRegisterMerchantDto toMerchantDTO() {
        QuickRegisterMerchantDto dto = new QuickRegisterMerchantDto();
        dto.setReferenceId(getId().toString());
        dto.setName(getName());
        dto.setPhone(getPhone());
        if (getActivityType() != null) {
            dto.setActivityType(getActivityType().toApayDTO());
        }
        dto.setAddress(getAddressDTO());
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
        dto.setLatitude(getLatitude());
        dto.setLongitude(getLongitude());
        return dto;
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(getId(), getName(), getAddress());
    }

    @Override
    public EntityType getHistoryType() {
        return EntityType.BRANCH;
    }

    public CommonDTO toCommonDTO() {
        return new CommonDTO(getId(), getName());
    }
}
