package uz.uzkassa.horeca.dto.compensation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompensationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8958393L;

    Long id;

    String description;

    Long productId;

    BigDecimal price;

    BigDecimal percent;

    Long branchId;

    Long categoryId;
}
