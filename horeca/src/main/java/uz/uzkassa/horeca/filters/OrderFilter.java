package uz.uzkassa.horeca.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.ReceiptPaymentType;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderFilter extends BaseFilter {

    Long clientId;

    Long tableId;

    Long waiterId;

    Long chefId;

    Long shiftId;

    String itemStatus;

    String excludeStatus;

    List<String> excludeStatuses;

    List<String> itemStatuses;

    @JsonProperty("isMine")
    boolean mine;

    @JsonProperty("withItems")
    boolean withItems;

    Integer itemsCount;

    BigDecimal fromTotalPrice;

    BigDecimal toTotalPrice;

    ReceiptPaymentType paymentType;

}
