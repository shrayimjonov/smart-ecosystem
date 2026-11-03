package uz.uzkassa.horeca.dto.cateringsettings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CateringServiceBatchDTO implements Serializable {

    Long hallId;

    BigDecimal feePercent;

    List<CateringServiceDTO> tables = new ArrayList<>();
}
