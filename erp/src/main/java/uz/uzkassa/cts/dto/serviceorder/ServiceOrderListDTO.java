package uz.uzkassa.cts.dto.serviceorder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.customer.CustomerShortDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ServiceOrderListDTO implements Serializable {

    Long id;

    String name;

    Double price;

    BigDecimal bonus;

    CustomerShortDTO customer;

    LocalDateTime createdDate;

    BigDecimal totalBonus;

    @JsonIgnore
    Long cusId;

    @JsonIgnore
    String cusName;

    @JsonIgnore
    String cusTin;

    public CustomerShortDTO getCustomer() {
        return new CustomerShortDTO(cusId, cusName, cusTin);
    }
}
