package uz.uzkassa.silen.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.CustomerBranchDTO;

@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "customer_branch")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicUpdate
public class CustomerBranch extends BaseIdentityEntity {
    @Column(name = "name")
    String name;

    @Column(name = "num")
    String num;

    @Column(name = "region_id")
    Long regionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", insertable = false, updatable = false)
    Region region;

    @Column(name = "district_id")
    String districtId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", insertable = false, updatable = false)
    District district;

    @Column(name = "director_tin")
    String directorTin;

    @Column(name = "director_name")
    String directorName;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    Customer customer;

    private Double lat;

    private Double lon;

    private String address;

    public CustomerBranchDTO toDTO() {
        CustomerBranchDTO dto = new CustomerBranchDTO();
        dto.setId(getId());
        dto.setName(getName());
        dto.setNum(getNum());
        dto.setLat(getLat());
        dto.setLon(getLon());
        dto.setAddress(getAddress());
        if (getRegion() != null) {
            dto.setRegionName(getRegion().getNameLocale());
            dto.setRegionId(getRegionId());
        }
        if (getDistrict() != null) {
            dto.setDistrictName(getDistrict().getNameLocale());
            dto.setDistrictId(getDistrictId());
        }
        dto.setCustomerId(getCustomerId());
        if (getCustomer() != null) {
            dto.setCustomer(getCustomer().toSelectItem());
        }
        return dto;
    }
}
