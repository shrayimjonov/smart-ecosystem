package uz.uzkassa.smartpos.domain.postgres.edocs.invoice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import uz.uzkassa.smartpos.dto.factura.FacturaProductDTO;
import uz.uzkassa.smartpos.dto.factura.MarkDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.Set;


@Entity
@Table(name = "invoice_item")
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
    private DraftInvoice invoice;

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

    @Column(name = "lgota_id")
    private Integer lgotaId;
    @Column(name = "lgota_name")
    private String lgotaName;
    @Column(name = "lgota_vat_sum", columnDefinition = "decimal default 0", precision = 25, scale = 2)
    private BigDecimal lgotaVatSum = BigDecimal.ZERO;
    @Column(name = "lgota_type")
    private Integer lgotaType;

    @Column(name = "name")
    private String name;

    @Column(name = "serial", length = 20)
    private String serial;

    @Column(name = "measure_id")
    private Long measureId;
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
    @Column(name = "origin", columnDefinition = "int default 2")
    private int origin = 2;

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
    private Long branchId;

    public FacturaProductDTO toProductDTO() {
        FacturaProductDTO dto = new FacturaProductDTO();
        dto.setOrdNo(getOrderNo());
        dto.setComittentName(getComittentName());
        dto.setComittentTin(getComittentTin());
        dto.setComittentVatRegCode(getComittentVatRegCode());
        dto.setCatalogCode(getCatalogCode());
        dto.setCatalogName(getCatalogName());
        dto.setBarcode(getBarcode());
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
        dto.setProfitRate(String.valueOf(getProfitRate()));
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
        if (getProductType() != null) {
            dto.setMarks(toMarkDTO());
        }
        dto.setBranchId(getBranchId());
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
}
