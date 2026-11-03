package uz.uzkassa.smartpos.integration.apay.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BillIdRequestDTO implements Serializable {
    String billId;
}
