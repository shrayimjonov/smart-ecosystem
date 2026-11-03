package uz.uzkassa.smartpossupply.dto.transaction;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.CurrencyType;

import java.util.HashSet;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 08.02.2023 10:09
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionDTO {

    Long id;

    String description;

    CurrencyType currency;

    @Size(min = 2)
    Set<TransactionItemDTO> items = new HashSet<>();

    public void addItem(TransactionItemDTO item) {
        items.add(item);
    }
}
