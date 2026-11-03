package uz.uzkassa.smartpossupply.dto.order.onec;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderContract1CDTO implements Serializable {

    String number;

    LocalDate date;

    public OrderContract1CDTO(String number, LocalDate date) {
        this.number = number;
        this.date = date;
    }
}
