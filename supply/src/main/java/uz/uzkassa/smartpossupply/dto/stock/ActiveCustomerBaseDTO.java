package uz.uzkassa.smartpossupply.dto.stock;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ActiveCustomerBaseDTO {
    Long customerId;
    String customerName;
    String[] regions;
    Long countTotalStock;
    Timestamp lastCreatedDate;

    public ActiveCustomerBaseDTO(Long customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }
}
