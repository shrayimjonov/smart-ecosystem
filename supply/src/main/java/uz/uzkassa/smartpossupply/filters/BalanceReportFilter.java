package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceReportFilter extends BaseFilter {

    Long categoryId;

    boolean hideZero;

    boolean hideZeroForReservation;

    Long committentId;

    Long productSerialId;

    String codeType;

    String reportType;
}
