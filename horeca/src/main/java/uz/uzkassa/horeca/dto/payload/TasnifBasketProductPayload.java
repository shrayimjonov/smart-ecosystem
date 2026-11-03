package uz.uzkassa.horeca.dto.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TasnifBasketProductPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 564564L;

    Long id;

    String tin;
}
