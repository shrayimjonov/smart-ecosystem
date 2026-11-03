package uz.uzkassa.silen.dto.partyaggregation;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.dto.BaseUuidDTO;
import uz.uzkassa.silen.dto.ProductCommonDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.PartyAggregationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class PartyAggregationDTO extends BaseUuidDTO {

    String number;

    @Size(min = 1)
    Set<String> parties;

    @Size(min = 1)
    Set<String> blockParties;

    String productId;

    @NotNull(message = "Выберите заказ")
    String orderId;

    String orderNumber;

    String blockOrderId;

    String blockOrderNumber;

    ProductCommonDTO product;

    Integer capacity;

    Integer quantity;

    String serialId;

    SelectItem serial;

    String storeId;

    String description;

    String gcp;

    PartyAggregationStatus status;

    String statusName;

    LocalDate productionDate = LocalDate.now();

    LocalDate expirationDate;

    boolean isOffline;

    List<AggregationStateMessage> codes;

    String errorMessage;

    PackageType unit = PackageType.BOX;

    boolean fully;
}
