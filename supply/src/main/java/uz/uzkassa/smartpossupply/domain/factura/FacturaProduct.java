package uz.uzkassa.smartpossupply.domain.factura;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerial;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.dto.factura.ExchangeInfoDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaProductDTO;
import uz.uzkassa.smartpossupply.dto.factura.MarkDTO;
import uz.uzkassa.smartpossupply.enums.ProductOrigin;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "factura_product",
        indexes = {
                @Index(columnList = "product_id", name = "factura_product_product_id_idx"),
                @Index(columnList = "factura_detail_id", name = "factura_product_factura_detail_id_idx")
        })
public class FacturaProduct extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 77700778304843L;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "discount_percent", precision = 25, scale = 2)
    BigDecimal discountPercent;

    @Column(name = "discount_amount", precision = 25, scale = 2)
    BigDecimal discountAmount;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_detail_id")
    FacturaDetail facturaDetail;

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

    @Column(name = "catalog_name", length = 2000)
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

    @Column(name = "product_serial_id")
    Long productSerialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_serial_id", updatable = false, insertable = false)
    ProductSerial productSerial;

    @Column(name = "measure_id")
    Long measureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measure_id", updatable = false, insertable = false)
    Unit measure;

    @Column(name = "package_code", length = 1000)
    String packageCode;

    @Column(name = "package_name", length = 1000)
    String packageName;

    @Column(name = "base_summa", precision = 25, scale = 2)
    BigDecimal baseSumma;

    @Column(name = "profit_rate", precision = 25, scale = 2)
    BigDecimal profitRate;

    @Column(name = "qty", precision = 25, scale = 2)
    BigDecimal qty;

    @Column(name = "summa", precision = 25, scale = 2)
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

    @Column(name = "s_order")
    Integer sOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "marks")
    Set<String> marks;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "aggregations")
    Set<String> aggregations;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "aggregation_blocks")
    Set<String> aggregationBlocks;

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

    @Column(name = "dispense_type")
    Integer dispenseType;

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


    public FacturaProductDTO toProductDTO() {
        FacturaProductDTO dto = new FacturaProductDTO();

        dto.setProductId(getProductId());
        if (getBranch() != null) {
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

        dto.setName(getName());
        dto.setSerial(getSerial());
        dto.setProductSerialId(getProductSerialId());
        if (StringUtils.isBlank(getPackageCode())) {
            dto.setMeasureId(getMeasureId());
        }
        dto.setPackageCode(getPackageCode());
        dto.setPackageName(getPackageName());
        dto.setBaseSumma(Optional.ofNullable(getBaseSumma()).orElse(BigDecimal.ZERO));
        dto.setProfitRate(Optional.ofNullable(getProfitRate()).orElse(BigDecimal.ZERO));
        dto.setCount(getQty());
        dto.setSumma(getSumma());
        dto.setExciseRate(getExciseRate());
        dto.setExciseSum(getExciseSum());
        dto.setDeliverySum(getDeliverySum());
        if (getVatRate() == null) {
            dto.setWithoutVat(Boolean.TRUE);
            dto.setVatRate(BigDecimal.ZERO);
            dto.setVatSum(BigDecimal.ZERO);
        } else {
            dto.setWithoutVat(getWithoutVat());
            dto.setVatRate(getVatRate());
            dto.setVatSum(getVatSum());
        }
        dto.setDeliverySumWithVat(getDeliverySumWithVat());
        dto.setExpiryDate(getExpiryDate());
        dto.setCode1C(getCode1C());
        if (getProduct() != null) {
            dto.setWeightBrutto(getProduct().getWeightBrutto());
            dto.setWeightNetto(getProduct().getWeightNetto());
            dto.setBoxBarcode(getProduct().getBoxBarcode());
            dto.setBlockBarcode(getProduct().getBlockBarcode());
            if (getProduct().getParent() != null) {
                dto.setParentProduct(getProduct().getParent().toProductCommonDTO());
                if (StringUtils.isEmpty(dto.getBlockBarcode())) {
                    dto.setBlockBarcode(getProduct().getParent().getBarcode());
                }
            }
            dto.setBlockProducts(getProduct().getBlockProducts());
        }
        if (getProductType() != null) {
            dto.setMarks(toMarkDTO());
            dto.setProductType(ProductType.buildByMarkingCode(getProductType()));
        }
        dto.setDispenseType(getDispenseType());
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
        boolean hasMark = false;
        if (!CollectionUtils.isEmpty(getMarks())) {
            markDTO.setKiz(getMarks());
            markDTO.setProductType(getProductType());
            hasMark = true;
        }
        if (!CollectionUtils.isEmpty(getAggregationBlocks())) {
            markDTO.setNomUpak(getAggregationBlocks());
            markDTO.setProductType(getProductType());
            hasMark = true;
        }
        if (!CollectionUtils.isEmpty(getAggregations())) {
            markDTO.setIdentTransUpak(getAggregations());
            markDTO.setProductType(getProductType());
            hasMark = true;
        }
        return hasMark ? markDTO : null;
    }

    public void fromMarkDTO(MarkDTO markDTO) {
        this.setProductType(markDTO.getProductType());
        this.setMarks(markDTO.getKiz());
        this.setAggregationBlocks(markDTO.getNomUpak());
        this.setAggregations(markDTO.getIdentTransUpak());
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
}
