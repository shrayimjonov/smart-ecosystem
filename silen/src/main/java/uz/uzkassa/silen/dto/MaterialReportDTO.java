package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialReportDTO {

    String productId;
    String productName;
    BigDecimal price;

    BigDecimal balanceStartBottle = BigDecimal.ZERO;
    BigDecimal balanceStartBottleSum = BigDecimal.ZERO;
    BigDecimal balanceStartBox = BigDecimal.ZERO;
    BigDecimal balanceStartPallet = BigDecimal.ZERO;

    BigDecimal productionBottle;
    BigDecimal productionBottleSum;
    BigDecimal productionBox;
    BigDecimal productionPallet;

    BigDecimal shippedBottle;
    BigDecimal shippedBottleSum;
    BigDecimal shippedBox;
    BigDecimal shippedPallet;

    BigDecimal balanceEndBottle = BigDecimal.ZERO;
    BigDecimal balanceEndBottleSum = BigDecimal.ZERO;
    BigDecimal balanceEndBox = BigDecimal.ZERO;
    BigDecimal balanceEndPallet = BigDecimal.ZERO;

    public MaterialReportDTO(String productId, String productName, BigDecimal price, BigDecimal productionBottle, BigDecimal shippedBottle, BigDecimal productionBox, BigDecimal shippedBox, BigDecimal productionPallet, BigDecimal shippedPallet) {
        this.productId = productId;
        this.productName = productName;
        this.price = Optional.ofNullable(price).orElse(BigDecimal.ZERO);
        this.productionBottle = productionBottle;
        this.productionBottleSum = this.price.multiply(productionBottle);
        this.shippedBottle = shippedBottle;
        this.shippedBottleSum = this.price.multiply(shippedBottle);

        this.productionBox = productionBox;
        this.shippedBox = shippedBox;
        this.productionPallet = productionPallet;
        this.shippedPallet = shippedPallet;

        this.balanceEndBottle = this.balanceEndBottle.add(this.productionBottle).subtract(this.shippedBottle);
        this.balanceEndBottleSum = this.price.multiply(this.balanceEndBottle);
        this.balanceEndBox = this.balanceEndBox.add(this.productionBox).subtract(this.shippedBox);
        this.balanceEndPallet = this.balanceEndPallet.add(this.productionPallet).subtract(this.shippedPallet);
    }
}
