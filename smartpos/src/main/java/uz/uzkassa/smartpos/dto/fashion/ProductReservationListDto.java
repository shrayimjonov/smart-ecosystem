package uz.uzkassa.smartpos.dto.fashion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.uzkassa.smartpos.domain.postgres.fashion.ProductReservation;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.product.ProductItemSearchDTO;
import uz.uzkassa.smartpos.enums.ReservationStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReservationListDto implements Serializable {
    private Long id;
    private CommonDTO branch;
    private ProductItemSearchDTO productItem;
    private CommonDTO product;
    private BigDecimal salesPrice;
    private String imageUrl;
    private BigDecimal qty;
    private String phoneNumber;
    private Integer hour;
    private LocalDateTime expireDate;
    private ReservationStatus status;

    public ProductReservationListDto(ProductReservation productReservation) {
        this.id = productReservation.getId();
        this.branch = new CommonDTO(productReservation.getBranch().getId(), productReservation.getBranch().getName());
        if (productReservation.getProductBranch() != null) {
            this.product = productReservation.getProductBranch().toSelectItem();
            this.salesPrice = productReservation.getProductBranch().getSalesPrice();
        }
        if (productReservation.getProductItem() != null) {
            this.productItem = new ProductItemSearchDTO(productReservation.getProductItem());
            this.salesPrice = productReservation.getProductItem().getSalesPrice();
        }
        this.imageUrl = productReservation.getImageUrl();
        this.qty = productReservation.getQty();
        this.phoneNumber = productReservation.getPhoneNumber();
        this.hour = productReservation.getHour();
        this.expireDate = productReservation.getExpireDate();
        this.status = productReservation.getStatus();
    }
}
