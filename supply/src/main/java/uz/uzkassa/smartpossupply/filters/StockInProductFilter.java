package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockInProductFilter extends BaseFilter {

    String aggregationCode;

    String markCode;

    String aggregationBlockCode;

    String transferType;

    String environmentType;

    String sortOrderType;

    Long companyCommittentId;
}
