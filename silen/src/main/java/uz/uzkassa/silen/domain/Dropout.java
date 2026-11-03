package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.DropoutListDTO;
import uz.uzkassa.silen.dto.ProductCommonDTO;
import uz.uzkassa.silen.enumeration.CodeType;
import uz.uzkassa.silen.enumeration.DropoutReason;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "dropout", indexes = {
        @Index(name = "dropout_code_idx", columnList = "code"),
        @Index(name = "dropout_product_id_idx", columnList = "product_id"),
        @Index(name = "dropout_dropout_reason_idx", columnList = "dropout_reason"),
        @Index(name = "dropout_organization_id_idx", columnList = "organization_id")
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dropout extends BaseOrganizationEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1123451234;

    @Column(name = "code")
    private String code;

    @Column(name = "print_code")
    private String printCode;

    @Column(name = "product_id")
    private String productId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    @Column(name = "serial_id")
    private String serialId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", updatable = false, insertable = false)
    private SerialNumber serialNumber;


    @Column(name = "code_id")
    private String codeId;

    @Column(name = "dropout_reason")
    @Enumerated(EnumType.STRING)
    private DropoutReason dropoutReason;

    @Column(name = "code_type")
    @Enumerated(EnumType.STRING)
    private CodeType codeType;

    @Column(name = "report_id")
    private String reportId;

    @Column(name = "amount", columnDefinition = "int4 default 0")
    private int amount;

    public DropoutListDTO toDTO() {
        DropoutListDTO dropoutListDTO = new DropoutListDTO();
        dropoutListDTO.setId(getId());
        dropoutListDTO.setCodeId(getCodeId());
        dropoutListDTO.setPrintCode(getPrintCode());
        dropoutListDTO.setCodeType(getCodeType());
        dropoutListDTO.setCreatedBy(getCreatedBy());
        dropoutListDTO.setCreatedDate(getCreatedDate());
        dropoutListDTO.setDropoutReason(getDropoutReason());
        dropoutListDTO.setAmount(getAmount());
        if (getDropoutReason() != null) {
            dropoutListDTO.setDropoutReasonNameRu(getDropoutReason().getNameRu());
        }
        dropoutListDTO.setReportId(getReportId());
        if (getProduct() != null) {
            dropoutListDTO.setProduct(new ProductCommonDTO(getProductId(), getProduct().getVisibleName(), getProduct().getBarcode()));
        }
        return dropoutListDTO;
    }
}
