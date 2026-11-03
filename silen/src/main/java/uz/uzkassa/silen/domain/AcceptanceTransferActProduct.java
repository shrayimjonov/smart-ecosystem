package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.act.AcceptanceTransferActProductSilenDTO;
import uz.uzkassa.silen.dto.act.ActProductItemDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "acceptance_transfer_act_product")
@SQLDelete(sql = "UPDATE acceptance_transfer_act_product SET deleted = 'true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AcceptanceTransferActProduct extends BaseOrganizationEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 234432423423L;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "acceptance_transfer_act_id")
    AcceptanceTransferAct acceptanceTransferAct;

    @Column(name = "product_id")
    String productId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "serial_id")
    String serialId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", updatable = false, insertable = false)
    SerialNumber serialNumber;
    @ColumnDefault("0.00")
    @Column(name = "price", precision = 25, scale = 2)
    BigDecimal price;

    @ColumnDefault("0.00")
    @Column(name = "cost", precision = 25, scale = 2)// quantity * price
    BigDecimal cost = BigDecimal.ZERO;
    @ColumnDefault("0.00")
    @Column(name = "discount", precision = 25, scale = 2)// cost / 100 * comission
    BigDecimal discount = BigDecimal.ZERO;
    @ColumnDefault("0.00")
    @Column(name = "payment_sum", precision = 25, scale = 2)// cost - comissionCost
    BigDecimal paymentSum = BigDecimal.ZERO;

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "shipment_item_id")
    Long shipmentItemId;

    public AcceptanceTransferActProductSilenDTO toDTO() {
        AcceptanceTransferActProductSilenDTO productDTO = new AcceptanceTransferActProductSilenDTO();
        productDTO.setId(getId());
        productDTO.setPrice(getPrice());
        productDTO.setQuantity(getQuantity());
        productDTO.setCost(getCost());
        productDTO.setDiscount(getDiscount());
        productDTO.setPaymentSum(getPaymentSum());
        if (getSerialNumber() != null) {
            productDTO.setSerial(getSerialNumber().toSelectItem());
        }
        if (getProduct() != null) {
            ActProductItemDTO actProductItemDTO = new ActProductItemDTO();
            actProductItemDTO.setId(getProduct().getId());
            actProductItemDTO.setBarcode(getProduct().getBarcode());
            actProductItemDTO.setPackageName(getProduct().getPackageName());
            actProductItemDTO.setName(getProduct().getVisibleName());
            productDTO.setProduct(actProductItemDTO);
        }
        return productDTO;
    }
}
