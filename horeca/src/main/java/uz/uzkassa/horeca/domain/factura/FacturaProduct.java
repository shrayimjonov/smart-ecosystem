package uz.uzkassa.horeca.domain.factura;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.base.BaseEntity;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.domain.warehouse.Warehouse;
import uz.uzkassa.horeca.dto.factura.ExchangeInfoDTO;
import uz.uzkassa.horeca.dto.factura.FacturaProductDTO;
import uz.uzkassa.horeca.dto.factura.MarkDTO;
import uz.uzkassa.horeca.enums.ProductOrigin;
import uz.uzkassa.horeca.enums.ProductType;
import uz.uzkassa.horeca.utils.MarkUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "factura_product",
    indexes = {
        @Index(columnList = "product_id", name = "factura_product_product_id_idx"),
        @Index(columnList = "factura_detail_id", name = "factura_product_factura_detail_id_idx"),
    }
)
public class FacturaProduct extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 77700778304843L;

    @Column(name = "factura_detail_id")
    Long facturaDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_detail_id", updatable = false, insertable = false)
    FacturaDetail facturaDetail;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "discount_percent", scale = 2, precision = 25)
    BigDecimal discountPercent;

    @Column(name = "discount_amount", scale = 2, precision = 25)
    BigDecimal discountAmount;

    @Column(name = "ord_no", length = 5)
    String ordNo;

    @Column(name = "code")
    String code;

    @Column(name = "committent_name")
    String committentName;

    @Column(name = "committent_tin")
    String committentTin;

    @Column(name = "committent_vat_reg_code")
    String committentVatRegCode;

    @Column(name = "committent_vat_reg_status")
    Integer committentVatRegStatus;

    @Column(name = "catalog_code")
    String catalogCode;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "catalog_name")
    String catalogName;

    @Column(name = "barcode")
    String barcode;

    @Column(name = "lgota_id")
    Integer lgotaId;

    @Column(name = "lgota_name", length = 1000)
    String lgotaName;

    @Column(name = "lgota_vat_sum")
    BigDecimal lgotaVatSum = BigDecimal.ZERO;

    @Column(name = "lgota_type")
    Integer lgotaType;

    @Column(name = "name", length = 2000)
    String name;

    @Column(name = "serial")
    String serial;

    @Column(name = "measure_id")
    Long measureId;

    @Column(name = "package_code", length = 1000)
    String packageCode;

    @Column(name = "package_name", length = 1000)
    String packageName;

    @Column(name = "base_summa", precision = 25, scale = 2)
    BigDecimal baseSumma;

    @Column(name = "profit_rate", precision = 25, scale = 2)
    BigDecimal profitRate;

    @Column(precision = 25, scale = 2)
    BigDecimal qty;

    @Column(precision = 25, scale = 2)
    BigDecimal summa;

    @Column(name = "excise_rate", precision = 25, scale = 2)
    BigDecimal exciseRate = BigDecimal.ZERO;

    @Column(name = "excise_sum", precision = 25, scale = 2)
    BigDecimal exciseSum = BigDecimal.ZERO;

    @Column(name = "delivery_sum", precision = 25, scale = 2)
    BigDecimal deliverySum;

    @Column(name = "vat_rate", precision = 25, scale = 2)
    BigDecimal vatRate;

    @Column(name = "vat_sum", precision = 25, scale = 2)
    BigDecimal vatSum;

    @Column(name = "delivery_sum_with_vat", precision = 25, scale = 2)
    BigDecimal deliverySumWithVat;

    @Column(name = "without_vat")
    Boolean withoutVat;

    @Column(name = "product_type")
    Integer productType;

    @Column(name = "origin")
    Integer origin = 2;

    @Column(name = "product_code", length = 18)
    String productCode;

    @Column(name = "product_properties", length = 300)
    String productProperties;

    @Column(name = "plan_position_id")
    Integer planPositionId;

    @Column(name = "expiry_date")
    LocalDate expiryDate;

    @Column(name = "code_1c")
    String code1C;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "kiz")
    Set<String> kiz; //Marks

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "nom_upak")
    Set<String> nomUpak; //aggregationBlocks

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ident_trans_upak")
    Set<String> identTransUpak; //aggregationCodes

    public void setIdentTransUpak(Set<String> identTransUpak) {
        this.identTransUpak = identTransUpak;
    }

    @Column(name = "warehouse_id")
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    public void setBarcode(String barcode) {
        this.barcode = barcode != null ? barcode.trim() : null;
    }

    public void setCatalogCode(String catalogCode) {
        this.catalogCode = catalogCode != null ? catalogCode.trim() : null;
    }

    public Integer getOrigin() {
        return origin != null ? origin : 2;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin != null ? origin : 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FacturaProduct)) {
            return false;
        }
        return id != null && id.equals(((FacturaProduct) o).id);
    }

    public FacturaProductDTO toProductDTO() {
        FacturaProductDTO dto = new FacturaProductDTO();

        dto.setProductId(getProductId());
        if (getBranchId() != null) {
            dto.setBranch(getBranch().toCommonDTO());
        }
        if (getWarehouse() != null) {
            dto.setWarehouse(getWarehouse().toCommonDTO());
        }

        dto.setOrdNo(getOrdNo());

        dto.setCommittentName(getCommittentName());
        dto.setCommittentTin(getCommittentTin());
        dto.setCommittentVatRegCode(getCommittentVatRegCode());
        dto.setCommittentVatRegStatus(getCommittentVatRegStatus());

        dto.setCatalogCode(getCatalogCode());
        dto.setCatalogName(getCatalogName());
        dto.setBarcode(getBarcode());

        dto.setLgotaId(getLgotaId());
        dto.setLgotaName(getLgotaName());
        dto.setLgotaType(getLgotaType());
        dto.setLgotaVatSum(Optional.ofNullable(getLgotaVatSum()).orElse(BigDecimal.ZERO));

        dto.setName(getName());
        dto.setSerial(getSerial());
        if (StringUtils.isBlank(getPackageCode())) {
            dto.setMeasureId(getMeasureId());
        }
        dto.setPackageCode(getPackageCode());
        dto.setPackageName(getPackageName());
        dto.setBaseSumma(getBaseSumma());
        dto.setProfitRate(getProfitRate());
        dto.setCount(getQty());
        dto.setSumma(getSumma());
        dto.setExciseRate(getExciseRate());
        dto.setExciseSum(getExciseSum());
        dto.setDeliverySum(getDeliverySum());
        dto.setVatRate(getVatRate());
        dto.setVatSum(getVatSum());
        dto.setDeliverySumWithVat(getDeliverySumWithVat());
        dto.setWithoutVat(getWithoutVat());
        dto.setExpiryDate(getExpiryDate());
        dto.setCode1C(getCode1C());
        if (getProductType() != null) {
            dto.setMarks(toMarkDTO());
            dto.setProductType(ProductType.buildByMarkingCode(getProductType()));
        }
        dto.setOrigin(getOrigin());
        if (ProductOrigin.getById(getOrigin()) != null) {
            dto.setOriginName(ProductOrigin.getById(getOrigin()).getName());
        }
        if (StringUtils.isNotEmpty(getProductCode())) {
            dto.setExchangeInfo(toExchangeInfo());
        }
        return dto;
    }

    public MarkDTO toMarkDTO() {
        MarkDTO markDTO = new MarkDTO();
        markDTO.setProductType(getProductType());
        markDTO.setKiz(getKiz());
        markDTO.setNomUpak(getNomUpak());
        markDTO.setIdentTransUpak(getIdentTransUpak());
        return markDTO;
    }

    public void fromMarkDTO(MarkDTO markDTO) {
        this.setProductType(markDTO.getProductType());
        this.setKiz(markDTO.getKiz());
        this.setNomUpak(markDTO.getNomUpak());
        this.setIdentTransUpak(markDTO.getIdentTransUpak());
    }

    public ExchangeInfoDTO toExchangeInfo() {
        ExchangeInfoDTO dto = new ExchangeInfoDTO();
        dto.setProductCode(getProductCode());
        dto.setProductProperties(getProductProperties());
        dto.setPlanPositionId(getPlanPositionId());
        return dto;
    }

    public void fromExchangeInfo(ExchangeInfoDTO exchangeInfoDTO) {
        this.setProductCode(exchangeInfoDTO.getProductCode());
        this.setProductProperties(exchangeInfoDTO.getProductProperties());
        this.setPlanPositionId(exchangeInfoDTO.getPlanPositionId());
    }

    public void fromCommittent(FacturaProductDTO facturaProductDTO) {
        setCommittentName(facturaProductDTO.getCommittentName());
        setCommittentTin(facturaProductDTO.getCommittentTin());
        setCommittentVatRegCode(facturaProductDTO.getCommittentVatRegCode());
        setCommittentVatRegStatus(facturaProductDTO.getCommittentVatRegStatus());
    }

    public void fromLgota(FacturaProductDTO facturaProductDTO) {
        setLgotaId(facturaProductDTO.getLgotaId());
        setLgotaName(facturaProductDTO.getLgotaName());
        setLgotaType(facturaProductDTO.getLgotaType());
        setLgotaVatSum(Optional.ofNullable(facturaProductDTO.getLgotaVatSum()).orElse(BigDecimal.ZERO));
    }
}
