package uz.uzkassa.smartpossupply.dto.shift;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZReportListDTO implements Serializable {

    Long id;

    CommonDTO shift;

    BigDecimal totalCash;

    BigDecimal totalCard;

    BigDecimal totalVat;

    LocalDateTime startDate;

    LocalDateTime endDate;
}
