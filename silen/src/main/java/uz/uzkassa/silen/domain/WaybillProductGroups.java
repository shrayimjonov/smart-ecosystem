package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.waybill.*;
import uz.uzkassa.silen.enumeration.TransportType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "waybill_product_group")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//@SQLDelete(sql = "UPDATE waybill_product_group SET deleted = 'true' WHERE id=?")
public class WaybillProductGroups implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    protected String id;

    @Column(name = "loading_trustee_pinfl")
    String loadingTrusteePinfl;
    @Column(name = "loading_trustee_name")
    String loadingTrusteeName;

    @Column(name = "unloading_trustee_pinfl")
    String unloadingTrusteePinfl;
    @Column(name = "unloading_trustee_name")
    String unloadingTrusteeName;

    @Column(name = "loading_station_id")
    String loadingStationId;
    @Column(name = "loading_station_name")
    String loadingStationName;

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
    @Column(name = "loading_address")
    String loadingAddress;
    @Column(name = "loading_longitude")
    Double loadingLongitude;
    @Column(name = "loading_latitude")
    Double loadingLatitude;

    @Column(name = "unloading_station_id")
    String unloadingStationId;
    @Column(name = "unloading_station_name")
    String unloadingStationName;

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
    @Column(name = "unloading_address")
    String unloadingAddress;
    @Column(name = "unloading_longitude")
    Double unloadingLongitude;
    @Column(name = "unloading_latitude")
    Double unloadingLatitude;

    @Column(name = "unloading_empowerment_id")
    String unloadingEmpowermentId;
    @Column(name = "unloading_empowerment_No")
    String unloadingEmpowermentNo;
    @Column(name = "unloading_empowerment_date_of_issue")
    LocalDate unloadingEmpowermentDateOfIssue;
    @Column(name = "unloading_empowerment_date_of_expire")
    LocalDate unloadingEmpowermentDateOfExpire;

    // ProductInfoDTO
    @Column(name = "total_delivery_sum")
    private BigDecimal totalDeliverySum;
    @ColumnDefault("0.00")
    @Column(name = "total_weight_brutto", precision = 25, scale = 5)
    private BigDecimal totalWeightBrutto;

    @Column(name = "waybill_transport_id", updatable = false, insertable = false)
    String waybillTransportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waybill_transport_id")
    WaybillTransport waybillTransport;

    @OneToMany(mappedBy = "productGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    List<WaybillProduct> products = new ArrayList<>();

    public List<WaybillProduct> getProducts() {
        if (products == null) {
            products = new ArrayList<>();
        }
        return products;
    }

    public void addProduct(WaybillProduct waybillProduct) {
        waybillProduct.setProductGroup(this);
        this.getProducts().add(waybillProduct);
    }


    public ProductGroupDTO toDTO(TransportType transportType) {
        ProductGroupDTO dto = new ProductGroupDTO();
        dto.setId(getId());
        if (getLoadingTrusteePinfl() != null) {
            dto.setLoadingTrustee(new PersonDTO(getLoadingTrusteePinfl(), getLoadingTrusteeName()));
        }
        if (getUnloadingTrusteePinfl() != null) {
            dto.setUnloadingTrustee(new PersonDTO(getUnloadingTrusteePinfl(), getUnloadingTrusteeName()));
        }

        Point loadingPoint = new Point();
        if (TransportType.Roadway == transportType) {
            loadingPoint.setRegionId(getLoadingRegionId());
            loadingPoint.setRegionName(getLoadingRegionName());
            loadingPoint.setDistrictCode(getLoadingDistrictId());
            loadingPoint.setDistrictName(getLoadingDistrictName());
            loadingPoint.setMahallaId(getLoadingMahallaId());
            loadingPoint.setMahallaName(getLoadingMahallaName());
            loadingPoint.setLongitude(getLoadingLongitude());
            loadingPoint.setLatitude(getLoadingLatitude());
            loadingPoint.setAddress(getLoadingAddress());
        } else {
            loadingPoint.setStationId(getLoadingStationId());
            loadingPoint.setStationName(getLoadingStationName());
        }
        dto.setLoadingPoint(loadingPoint);


        Point unloadingPoint = new Point();
        if (TransportType.Roadway == transportType) {
            unloadingPoint.setRegionId(getUnloadingRegionId());
            unloadingPoint.setRegionName(getUnloadingRegionName());
            unloadingPoint.setDistrictCode(getUnloadingDistrictId());
            unloadingPoint.setDistrictName(getUnloadingDistrictName());
            unloadingPoint.setMahallaId(getUnloadingMahallaId());
            unloadingPoint.setMahallaName(getUnloadingMahallaName());
            unloadingPoint.setLongitude(getUnloadingLongitude());
            unloadingPoint.setLatitude(getUnloadingLatitude());
            unloadingPoint.setAddress(getUnloadingAddress());
        } else {
            unloadingPoint.setStationId(getUnloadingStationId());
            unloadingPoint.setStationName(getUnloadingStationName());
        }
        dto.setUnloadingPoint(unloadingPoint);

        if (getUnloadingEmpowermentNo() != null) {
            UnloadingEmpowermentDTO empowermentDTO = new UnloadingEmpowermentDTO();
            empowermentDTO.setEmpowermentId(getUnloadingEmpowermentId());
            empowermentDTO.setEmpowermentNo(getUnloadingEmpowermentNo());
            empowermentDTO.setEmpowermentDateOfExpire(getUnloadingEmpowermentDateOfExpire());
            empowermentDTO.setEmpowermentDateOfIssue(getUnloadingEmpowermentDateOfIssue());
            dto.setUnloadingEmpowermentDTO(empowermentDTO);
        }

        ProductInfoDTO productInfoDTO = new ProductInfoDTO();
        productInfoDTO.setTotalDeliverySum(getTotalDeliverySum());
        productInfoDTO.setTotalWeightBrutto(getTotalWeightBrutto());
        if (CollectionUtils.isNotEmpty(getProducts())) {
            List<Products> productsCollection = getProducts().stream().map(WaybillProduct::toDTO)
                    .collect(Collectors.toList());
            productInfoDTO.setProducts(productsCollection);
        }
        dto.setProductInfoDTO(productInfoDTO);
        return dto;
    }
}
