package uz.uzkassa.smartpossupply.dto.stockadjustment;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WithdrawRequestDTO implements Serializable {

    String document;

    String signature;
}
