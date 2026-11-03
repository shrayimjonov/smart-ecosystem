package uz.uzkassa.smartpossupply.dto.receipt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpossupply.dto.product.TerminalProductListDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 08.10.2024
 */

@Getter
@Setter
@ToString
public class TerminalReceiptItemDTO implements Serializable {

    Long id;

    String comittentTin;

    TerminalProductListDTO product;

    BigDecimal qty;

    BigDecimal totalPrice;

    BigDecimal totalVatPrice;

    BigDecimal vatRate;

    BigDecimal vatPrice;

    BigDecimal totalDiscountPrice;

    BigDecimal discountPercent;

    BigDecimal discountPrice;

    Set<String> marks = new HashSet<>();

    Set<String> aggregationBlockCodes = new HashSet<>();

    //only for backend
    @JsonIgnore
    UUID uid;

//    @JsonIgnore
    BigDecimal markQty;

}
