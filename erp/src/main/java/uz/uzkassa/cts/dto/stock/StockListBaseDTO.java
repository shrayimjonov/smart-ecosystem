package uz.uzkassa.cts.dto.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.enums.StockStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class StockListBaseDTO implements Serializable {

    Long id;

    String stockNumber;

    LocalDateTime stockDate;

    CompanyCommonDTO company;

    CommonDTO branch;

    CommonDTO department;

    CommonDTO warehouse;

    StockStatus status;
}
