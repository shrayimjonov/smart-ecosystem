package uz.uzkassa.smartpossupply.dto.aggregationcodes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationCodeDTO implements Serializable {

    LocalDate stockDate;

    List<AggregationCodeItemDTO> items;
}
