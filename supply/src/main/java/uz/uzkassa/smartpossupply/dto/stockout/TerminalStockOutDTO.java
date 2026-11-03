package uz.uzkassa.smartpossupply.dto.stockout;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 17.10.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalStockOutDTO implements Serializable {

    Long id;

    Long contractId;

    Long customerId;

    Long deliveryBranchId;

    String description;

    PaymentType paymentType;

    List<TerminalStockOutProductDTO> products = new ArrayList<>();

    @JsonIgnore
    public void addProduct(TerminalStockOutProductDTO stockProductDTO) {
        products.add(stockProductDTO);
    }
}
