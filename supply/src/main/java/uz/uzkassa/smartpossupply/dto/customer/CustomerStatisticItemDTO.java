package uz.uzkassa.smartpossupply.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerStatisticItemDTO implements Serializable {

    String dataType;

    Long total;

    @SuppressWarnings("unused")
    public CustomerStatisticItemDTO(String dataType, Long total) {
        this.dataType = dataType;
        this.total = total;
    }
}
