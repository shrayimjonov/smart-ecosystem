package uz.uzkassa.smartpossupply.domain.waybill;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillPersonDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillPointDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillProductGroupDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillProductInfoDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillUnloadingEmpowermentDTO;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "waybill_product_group",
        indexes = {
                @Index(columnList = "waybill_transport_id", name = "waybill_product_group_waybill_transport_id_idx")
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE waybill_product_group SET deleted = true WHERE id=?")
public class WaybillProductGroup extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 534972586L;

    @Column(name = "loading_trustee_pinfl")
    String loadingTrusteePinfl;

    @Column(name = "loading_trustee_name")
    String loadingTrusteeName;

    @Column(name = "unloading_trustee_pinfl")
    String unloadingTrusteePinfl;

    @Column(name = "unloading_trustee_name")
    String unloadingTrusteeName;

    @Column(name = "loading_region_id")
    Integer loadingRegionId;

    @Column(name = "loading_region_name")
    String loadingRegionName;

    @Column(name = "loading_district_id")
    Integer loadingDistrictId;

    @Column(name = "loading_district_name")
    String loadingDistrictName;

    @Column(name = "loading_mahalla_id")
    Integer loadingMahallaId;

    @Column(name = "loading_mahalla_name")
    String loadingMahallaName;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "loading_address")
    String loadingAdress;

    @Column(name = "loading_longitude", precision = 20)
    Double loadingLongitude;

    @Column(name = "loading_latitude", precision = 20)
    Double loadingLatitude;

    @Column(name = "unloading_region_id")
    Integer unloadingRegionId;

    @Column(name = "unloading_region_name")
    String unloadingRegionName;

    @Column(name = "unloading_district_id")
    Integer unloadingDistrictId;

    @Column(name = "unloading_district_name")
    String unloadingDistrictName;

    @Column(name = "unloading_mahalla_id")
    Integer unloadingMahallaId;

    @Column(name = "unloading_mahalla_name")
    String unloadingMahallaName;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "unloading_address")
    String unloadingAddress;

    @Column(name = "unloading_longitude", precision = 20)
    Double unloadingLongitude;

    @Column(name = "unloading_latitude", precision = 20)
    Double unloadingLatitude;

    @Column(name = "unloading_empowerment_id")
    String unloadingEmpowermentId;

    @Column(name = "unloading_empowerment_no")
    String unloadingEmpowermentNo;

    @Column(name = "unloading_empowerment_date_of_issue")
    LocalDate unloadingEmpowermentDateOfIssue;

    @Column(name = "unloading_empowerment_date_of_expire")
    LocalDate unloadingEmpowermentDateOfExpire;

    // ProductInfoDTO
    @Column(name = "total_delivery_sum", precision = 25, scale = 2)
    BigDecimal totalDeliverySum;

    @Column(name = "total_weight_brutto", scale = 5, precision = 25)
    BigDecimal totalWeightBrutto;

    @Column(name = "waybill_transport_id")
    Long waybillTransportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waybill_transport_id", updatable = false, insertable = false)
    WaybillTransport waybillTransport;

    @OneToMany(mappedBy = "productGroup", cascade = CascadeType.ALL)
    @SQLRestriction("deleted = false")
    List<WaybillProduct> products = new ArrayList<>();

    public void addProduct(WaybillProduct product) {
        this.products.add(product);
    }

    public WaybillProductGroupDTO toDTO() {
        WaybillProductGroupDTO dto = new WaybillProductGroupDTO();
        dto.setId(getId());
        if (StringUtils.hasText(getLoadingTrusteePinfl())) {
            dto.setLoadingTrustee(new WaybillPersonDTO(getLoadingTrusteePinfl(), getLoadingTrusteeName()));
        }
        if (StringUtils.hasText(getUnloadingTrusteePinfl())) {
            dto.setUnloadingTrustee(new WaybillPersonDTO(getUnloadingTrusteePinfl(), getUnloadingTrusteeName()));
        }

        WaybillPointDTO loadingPoint = new WaybillPointDTO();
        loadingPoint.setRegionId(getLoadingRegionId());
        loadingPoint.setRegionName(getLoadingRegionName());
        loadingPoint.setDistrictCode(getLoadingDistrictId());
        loadingPoint.setDistrictName(getLoadingDistrictName());
        loadingPoint.setMahallaId(getLoadingMahallaId());
        loadingPoint.setLongitude(getLoadingLongitude());
        loadingPoint.setLatitude(getLoadingLatitude());
        loadingPoint.setAddress(getLoadingAdress());
        dto.setLoadingPoint(loadingPoint);

        WaybillPointDTO unloadingPoint = new WaybillPointDTO();
        unloadingPoint.setRegionId(getUnloadingRegionId());
        unloadingPoint.setRegionName(getUnloadingRegionName());
        unloadingPoint.setDistrictCode(getUnloadingDistrictId());
        unloadingPoint.setDistrictName(getUnloadingDistrictName());
        unloadingPoint.setMahallaId(getUnloadingMahallaId());
        unloadingPoint.setLongitude(getUnloadingLongitude());
        unloadingPoint.setLatitude(getUnloadingLatitude());
        unloadingPoint.setAddress(getUnloadingAddress());
        dto.setUnloadingPoint(unloadingPoint);

        if (getUnloadingEmpowermentId() != null) {
            WaybillUnloadingEmpowermentDTO empowermentDTO = new WaybillUnloadingEmpowermentDTO();
            empowermentDTO.setEmpowermentId(getUnloadingEmpowermentId());
            empowermentDTO.setEmpowermentNo(getUnloadingEmpowermentNo());
            empowermentDTO.setEmpowermentDateOfExpire(getUnloadingEmpowermentDateOfExpire());
            empowermentDTO.setEmpowermentDateOfIssue(getUnloadingEmpowermentDateOfIssue());
            dto.setUnloadingEmpowermentDTO(empowermentDTO);
        }


        WaybillProductInfoDTO productInfoDTO = new WaybillProductInfoDTO();
        productInfoDTO.setTotalDeliverySum(getTotalDeliverySum());
        productInfoDTO.setTotalWeightBrutto(getTotalWeightBrutto());
        if (!CollectionUtils.isEmpty(getProducts())) {
            productInfoDTO.setProducts(getProducts().stream().map(WaybillProduct::toDTO).toList());
            dto.setProductInfoDTO(productInfoDTO);
        }
        return dto;
    }
}
