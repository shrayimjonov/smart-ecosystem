package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;


@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
public class TerminalReceiptDetailGeneralDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8678745564L;

    Long id;

    Long productId;

    @Size(max = 255)
    String productName;

    @Size(max = 255)
    String productBarcode;

    @Size(max = 255)
    String vatBarcode;

    Long unitId;

    String unitName;

    BigDecimal excise;

    BigDecimal exciseRate;

    @NotNull
    BigDecimal price;

    BigDecimal salesPrice;

    @NotNull
    BigDecimal qty;

    @NotNull
    BigDecimal amount;

    BigDecimal discountPercent;

    BigDecimal discount;

    BigDecimal ndsPercent;

    BigDecimal nds;

    Set<String> marks;

    Integer origin;

    boolean hasMark;
}
