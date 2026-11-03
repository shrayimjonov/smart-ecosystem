package uz.uzkassa.silen.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.invoice.ExchangeInfoDTO;
import uz.uzkassa.silen.dto.invoice.FacturaProductDTO;
import uz.uzkassa.silen.dto.invoice.MarkDTO;
import uz.uzkassa.silen.enumeration.ProductOrigin;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.Set;


@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "invoice_item")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
public class InvoiceItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Column(name = "order_no", length = 5)
    private String orderNo;

    @Column(name = "comittent_name", length = 50)
    private String comittentName;

    @Column(name = "comittent_tin", length = 9)
    private String comittentTin;

    @Column(name = "comittent_vat_reg_code", length = 12)
    private String comittentVatRegCode;

    @Column(name = "catalog_code", length = 32)
    private String catalogCode;

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "catalog_name")
    private String catalogName;

    @Column(name = "barcode", length = 32)
    private String barcode;

    @Column(name = "warehouse_id")
    private Integer warehouseId;

    @Column(name = "lgota_id")
    private Integer lgotaId;
    @Column(name = "lgota_name")
    private String lgotaName;

    @ColumnDefault("0.00")
    @Column(name = "lgota_vat_sum", precision = 25, scale = 2)
    private BigDecimal lgotaVatSum = BigDecimal.ZERO;
    @Column(name = "lgota_type")
    private Integer lgotaType;

    @Column(name = "name")
    private String name;

    @Column(name = "serial")
    private String serial;

    @Column(name = "measure_id")
    private Long measureId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "measure_id", updatable = false, insertable = false)
    private Measure measure;

    @Column(name = "package_code")
    private String packageCode;
    @Column(name = "package_name")
    private String packageName;

    @Column(name = "base_price", precision = 25, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "profit_rate", precision = 25, scale = 2)
    private BigDecimal profitRate;

    @Column(name = "price", precision = 25, scale = 2)
    private BigDecimal price;

    @Column(precision = 25, scale = 2)
    private BigDecimal qty;


    @Column(name = "excise_rate", precision = 25, scale = 2)
    private BigDecimal exciseRate;

    @Column(name = "excise_total", precision = 25, scale = 2)
    private BigDecimal exciseTotal;

    @Column(name = "subtotal", precision = 25, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "vat_rate", precision = 25, scale = 2)
    private BigDecimal vatRate;

    @Column(name = "vat_total", precision = 25, scale = 2)
    private BigDecimal vatTotal;

    @Column(name = "total", precision = 25, scale = 2)
    private BigDecimal total;

    private Boolean withoutVat;

    @ColumnDefault("1")
    @Column(name = "origin")
    private int origin = 1;

    @Column(name = "product_code", length = 18)
    private String productCode;
    @Column(name = "product_properties", length = 300)
    private String productProperties;
    @Column(name = "plan_position_id")
    private Integer planPositionId;

    @Column(name = "product_type")
    private Integer productType;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "kiz")
    private Set<String> kiz;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "nom_upak")
    private Set<String> nomUpak;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "trans_upak")
    private Set<String> identTransUpak;

    @Column(name = "has_discount", columnDefinition = "boolean default false")
    private boolean hasDiscount = false;

    public FacturaProductDTO toProductDTO() {
        FacturaProductDTO dto = new FacturaProductDTO();
        dto.setOrdNo(getOrderNo());
        dto.setInvoiceItemId(getId());
        dto.setComittentName(getComittentName());
        dto.setComittentTin(getComittentTin());
        dto.setComittentVatRegCode(getComittentVatRegCode());
        dto.setCatalogCode(getCatalogCode());
        dto.setCatalogName(getCatalogName());
        dto.setBarcode(getBarcode());
        dto.setWarehouseId(getWarehouseId());
        dto.setLgotaId(getLgotaId());
        dto.setLgotaName(getLgotaName());
        dto.setLgotaVatSum(getLgotaVatSum());
        dto.setLgotaType(getLgotaType());
        dto.setName(getName());
        dto.setSerial(getSerial());
        dto.setMeasureId(getMeasureId());
        dto.setPackageCode(getPackageCode());
        dto.setPackageName(getPackageName());
        dto.setBaseSumma(getBasePrice());
        dto.setProfitRate(getProfitRate());
        dto.setCount(getQty());
        dto.setSumma(getPrice());
        dto.setExciseRate(getExciseRate());
        dto.setExciseSum(getExciseTotal());
        dto.setDeliverySum(getSubtotal());
        dto.setVatRate(getVatRate());
        dto.setVatSum(getVatTotal());
        dto.setDeliverySumWithVat(getTotal());
        dto.setWithoutVat(getWithoutVat());
        dto.setOrigin(getOrigin());
        dto.setOriginName(ProductOrigin.fromCode(getOrigin()).getName());
        dto.setHasDiscount(isHasDiscount());
        if (getProductType() != null) {
            dto.setMarks(toMarkDTO());
        }
        if (StringUtils.isNotEmpty(getProductCode())) {
            dto.setExchangeInfo(toExchangeInfo());
        }
        return dto;
    }

    private MarkDTO toMarkDTO() {
        MarkDTO markDTO = new MarkDTO();
        markDTO.setProductType(getProductType());
        markDTO.setKiz(getKiz());
        markDTO.setNomUpak(getNomUpak());
        markDTO.setIdentTransUpak(getIdentTransUpak());
        return markDTO;
    }

    public void fromMarkDTO(MarkDTO marks) {
        this.setProductType(marks.getProductType());
        this.setKiz(marks.getKiz());
        this.setNomUpak(marks.getNomUpak());
        this.setIdentTransUpak(marks.getIdentTransUpak());
    }

    private ExchangeInfoDTO toExchangeInfo() {
        ExchangeInfoDTO dto = new ExchangeInfoDTO();
        dto.setProductCode(getProductCode());
        dto.setProductProperties(getProductProperties());
        dto.setPlanPositionId(getPlanPositionId());
        return dto;
    }

    public void fromExchangeInfo(ExchangeInfoDTO dto) {
        this.setProductCode(dto.getProductCode());
        this.setProductProperties(dto.getProductProperties());
        this.setPlanPositionId(dto.getPlanPositionId());
    }
}
