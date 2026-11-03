package uz.uzkassa.smartpossupply.dto.receipt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class SmartposReceiptItemDTO implements Serializable {

    @JsonIgnore
    UUID uid;

    String committentTin;

    @Size(max = 255)
    String productName;

    @Size(max = 255)
    String productBarcode;

    @Size(max = 255)
    String vatBarcode;

    String categoryName;

    Long unitId;
    String unitName;

    BigDecimal price;

    BigDecimal salesPrice;

    BigDecimal qty;

    BigDecimal amount;

    BigDecimal discountPercent;

    BigDecimal discount;

    BigDecimal ndsPercent;

    BigDecimal nds;

    BigDecimal vatPrice;

    BigDecimal markQty = BigDecimal.ZERO;

    Long branchId;

    SmartposReceiptItemExtraDetailDTO extraDetail;

    Set<String> marks = new HashSet<>();
}
