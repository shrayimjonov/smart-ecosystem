package uz.uzkassa.smartpossupply.dto.aggregation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationCreateDTO implements Serializable {

    Long productId;

    Integer qty;

    String printCode;

    Set<String> marks = new LinkedHashSet<>();

    String description;

    LocalDate productionDate;

    LocalDate expiredDate;

    Integer qtyInUnit;
}
