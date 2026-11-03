package uz.uzkassa.smartpossupply.dto.competitorproduct;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompetitorProductDTO implements Serializable {

    String name;

    Integer qty;

    BigDecimal price;

    Long customerBranchId;
}
