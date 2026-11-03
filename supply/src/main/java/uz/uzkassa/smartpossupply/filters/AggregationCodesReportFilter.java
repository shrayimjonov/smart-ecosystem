package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.03.2025
 */
@Getter
@Setter
public class AggregationCodesReportFilter extends BaseFilter {

    boolean hideZero;

    Long committentId;

    Long productSerialId;

    Long productContractId;

    String codeType;

    String reportType;

    Long palletId;

    Long boxId;

    Long blockId;

    boolean parent;
}
