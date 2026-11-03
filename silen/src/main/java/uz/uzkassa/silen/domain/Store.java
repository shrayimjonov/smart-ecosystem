package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.StoreDTO;
import uz.uzkassa.silen.enumeration.StoreStatus;

import java.io.Serial;
import java.io.Serializable;

/**
 * A Stock.
 */
@Getter
@Setter
@Entity
@ToString
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "store", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "organization_id", "deleted"})
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE store SET deleted = 'true' WHERE id=?")
public class Store extends BaseIdentityEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "organization_id")
    private String organizationId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", updatable = false, insertable = false)
    private Organization organization;

    private String address;

    @Column(name = "district_id")
    private String districtId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", updatable = false, insertable = false)
    private District district;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StoreStatus status;

    @Column(name = "personal")
    private String personal;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "personal", updatable = false, insertable = false)
    private User personalUser;

    @Column(name = "main", columnDefinition = "boolean default false")
    private boolean main = false;

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(getId().toString(), getName());
    }

    public StoreDTO toDTO() {
        StoreDTO dto = new StoreDTO();
        dto.setId(getId());
        dto.setName(getName());
        dto.setBranchCode(getBranchCode());
        dto.setStatus(getStatus());
        if (getStatus() != null) {
            dto.setStatusName(getStatus().getNameRu());
        }
        if (getPersonalUser() != null) {
            dto.setPersonalUser(getPersonalUser().toSelectItem());
        }

        dto.setOrganizationId(getOrganizationId());
        if (getOrganization() != null) {
            dto.setOrganizationName(getOrganization().getName());
        }
        dto.setDistrictId(getDistrictId());
        if (getDistrict() != null) {
            dto.setDistrictName(getDistrict().getNameLocale());
            dto.setRegionId(getDistrict().getRegionId());
            if (getDistrict().getRegion() != null) {
                dto.setRegionName(getDistrict().getRegion().getNameLocale());
            }
        }
        dto.setMain(isMain());
        dto.setAddress(getAddress());
        return dto;
    }
}
