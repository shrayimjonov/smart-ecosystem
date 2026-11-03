package uz.uzkassa.smartpos.dto.fashion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.uzkassa.smartpos.domain.postgres.fashion.ProductReservation;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.catalog.ProductDTO;
import uz.uzkassa.smartpos.dto.product.ProductItemDTO;
import uz.uzkassa.smartpos.enums.ReservationStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReservationDetailDto implements Serializable {
    private Long id;
    private CommonDTO branch;
    private ProductItemDTO productItem;
    private ProductDTO product;
    private BigDecimal qty;
    private String phoneNumber;
    private Integer hour;
    private String imageUrl;
    private LocalDateTime expireDate;
    private ReservationStatus status;
    private BigDecimal stockAmount;

    public ProductReservationDetailDto(ProductReservation productReservation) {
        this.id = productReservation.getId();
        this.branch = new CommonDTO(productReservation.getBranch().getId(), productReservation.getBranch().getName());
        if (productReservation.getProductItem() != null) {
            this.productItem = productReservation.getProductItem().getDTO();
        }
        if (productReservation.getProductBranch() != null) {
            this.product = productReservation.getProductBranch().getDTO(true);
        }
        this.qty = productReservation.getQty();
        this.phoneNumber = productReservation.getPhoneNumber();
        this.hour = productReservation.getHour();
        this.imageUrl = productReservation.getImageUrl();
        this.expireDate = productReservation.getExpireDate();
        this.status = productReservation.getStatus();
    }
}
