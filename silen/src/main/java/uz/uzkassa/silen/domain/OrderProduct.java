package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.marking.OrderProductDTO;
import uz.uzkassa.silen.enumeration.BufferStatus;
import uz.uzkassa.silen.enumeration.CisType;
import uz.uzkassa.silen.enumeration.RateType;
import uz.uzkassa.silen.enumeration.SerialNumberType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * A Order.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "order_product",
        indexes = {
                @Index(columnList = "buffer_status", name = "buffer_status_idx")
        }
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OrderProduct extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 13549815315L;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "order_id", insertable = false, updatable = false)
    private String orderId;

    @Column(name = "product_id")
    private String productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "rate_type")
    private RateType rateType = RateType.STANDARD;

    private Integer quantity;

    @Column(columnDefinition = "int4 default 0")
    private Integer received = 0;

    @Column(columnDefinition = "int4 default 0")
    private Integer remaining = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "serial_number_type")
    private SerialNumberType serialNumberType = SerialNumberType.OPERATOR;

    @Enumerated(EnumType.STRING)
    @Column(name = "cis_type")
    private CisType cisType = CisType.UNIT;

    @Enumerated(EnumType.STRING)
    @Column(name = "buffer_status")
    private BufferStatus bufferStatus;

    @Column(name = "last_block_id")
    private String lastBlockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    @Column(name = "error_message")
    private String errorMessage;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "orderProduct")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<Party> parties;

    public OrderProductDTO toDTO() {
        OrderProductDTO dto = setAuditValues(new OrderProductDTO());
        dto.setId(getId());
        if (getOrder() != null) {
            dto.setTuronId(getOrder().getTuronId());
            dto.setOrderNumber(getOrder().getNumber());
            if (getOrder().getReleaseMethodType() != null) {
                dto.setReleaseMethodType(getOrder().getReleaseMethodType());
                dto.setReleaseMethodTypeName(getOrder().getReleaseMethodType().getNameRu());
            }
            if (getOrder().getOrganization() != null) {
                dto.setOrganizationId(getOrder().getOrganization().getId());
                dto.setOrganizationName(getOrder().getOrganization().getName());
                dto.setOrganizationTin(getOrder().getOrganization().getTin());
            }
        }

        if (getBufferStatus() != null) {
            dto.setBufferStatus(getBufferStatus());
            dto.setBufferStatusName(getBufferStatus().getNameRu());
        }
        dto.setProductId(getProductId());
        if (getProduct() != null) {
            dto.setProductName(getProduct().getVisibleName());
            dto.setProductType(getProduct().getType().name());
            dto.setProductGroup(getProduct().getType().getProductGroup().name());
            dto.setGtin(getProduct().getBarcode());
            dto.setTemplateId(getProduct().getType().getTemplateId());
        }
        dto.setQuantity(getQuantity());
        dto.setReceived(getReceived());
        dto.setRemaining(getRemaining());
        dto.setSerialNumberType(getSerialNumberType());
        dto.setCisType(Optional.ofNullable(getCisType()).orElse(CisType.UNIT));
        dto.setCisTypeNme(dto.getCisType().getNameRu());
        return dto;
    }

    @Override
    public SelectItem toSelectItem() {
        if (getProduct() != null) {
            return new SelectItem(this.getId(), this.getOrder().getNumber(), this.getProduct().getVisibleName());
        } else {
            return new SelectItem(this.getId(), "");
        }
    }
}
