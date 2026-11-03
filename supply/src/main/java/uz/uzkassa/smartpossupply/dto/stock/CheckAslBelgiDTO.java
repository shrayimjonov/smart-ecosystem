package uz.uzkassa.smartpossupply.dto.stock;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckAslBelgiDTO implements Serializable {

    Long stockId;

    Long stockProductId;

    String token;

    boolean fromFactura;

    boolean xtraceEnabled;
}
