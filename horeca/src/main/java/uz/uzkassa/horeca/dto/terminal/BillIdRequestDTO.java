package uz.uzkassa.horeca.dto.terminal;

import java.io.Serializable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BillIdRequestDTO implements Serializable {
    String billId;
}
