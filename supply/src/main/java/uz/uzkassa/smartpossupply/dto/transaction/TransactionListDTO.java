package uz.uzkassa.smartpossupply.dto.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 08.02.2023 11:39
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionListDTO {

    Long id;

    String transactionNumber;

    TransactionStatus status;

    CurrencyType currency;

    LocalDateTime createdDate;

    List<TransactionItemListDTO> items = new ArrayList<>();

    @JsonIgnore
    public void addItem(TransactionItemListDTO item) {
        items.add(item);
    }
}
