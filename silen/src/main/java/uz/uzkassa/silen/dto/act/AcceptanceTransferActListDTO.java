package uz.uzkassa.silen.dto.act;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.SelectItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptanceTransferActListDTO {

    String id;

    String actId;

    String actNumber;

    LocalDate actDate;

    LocalDateTime createdDate;

    String description;

    SelectItem customer;

    SelectItem contract;

    BigDecimal comission;

    BigDecimal totalCost;

    SelectItem status;

    SelectItem store;
}
