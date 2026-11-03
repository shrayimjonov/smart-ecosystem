package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.warehouse.CustomerCommonDTO;
import uz.uzkassa.silen.dto.warehouse.CustomerDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A Customer.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "customer")
@SQLDelete(sql = "UPDATE customer SET deleted = true WHERE id = ?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NoArgsConstructor
public class Customer extends BaseIdentityEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 121413241L;

    @NotNull
    @Column(name = "organization_id", nullable = false)
    private String organizationId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", updatable = false, insertable = false)
    private Organization organization;

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
    private Double lat;
    private Double lon;

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

    @Column(name = "has_financial_discount", columnDefinition = "bool default false")
    private boolean hasFinancialDiscount = false;

    @Column(name = "is_comitent", columnDefinition = "bool default false")
    private boolean isCommittent;

    @ColumnDefault("0.00")
    @Column(name = "comission", precision = 25, scale = 2)
    private BigDecimal comission = BigDecimal.ZERO;

    @OneToMany(mappedBy = "customer")
    @OrderBy(value = " date desc")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    List<Contract> contracts = new ArrayList<>();

    public CustomerDTO toDto() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(getId());
        dto.setCommittent(this.isCommittent());
        dto.setComission(getComission());
        dto.setHasFinancialDiscount(isHasFinancialDiscount());
        dto.setTin(getTin());
        dto.setName(getName());
        dto.setMobile(getMobile());
        dto.setWorkPhone(getWorkPhone());
        dto.setDirector(getDirector());
        dto.setOked(getOked());
        dto.setAccountant(getAccountant());
        dto.setAccount(getAccount());
        if (getBank() != null) {
            dto.setBankId(getBankId());
            dto.setBankName(getBank().getNameRu());
        }
        dto.setAddress(getAddress());
        if (getDistrict() != null) {
            dto.setDistrictId(getDistrictId());
            dto.setDistrictName(getDistrict().getNameLocale());
            if (getDistrict().getRegion() != null) {
                dto.setRegionId(getDistrict().getRegionId());
                dto.setRegionName(getDistrict().getRegion().getNameRu());
            }
        }
        return dto;
    }

    public CustomerCommonDTO toCommonDto() {
        CustomerCommonDTO dto = new CustomerCommonDTO();
        dto.setId(getId());
        dto.setTin(getTin());
        dto.setName(getName());
        return dto;
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(String.valueOf(getId()), getName(), String.valueOf(this.isCommittent()));
    }
}
