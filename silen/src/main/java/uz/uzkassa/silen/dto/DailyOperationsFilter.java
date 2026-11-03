package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.DailyOperationTypes;
import uz.uzkassa.silen.enumeration.PackageType;

@Getter
@Setter
public class DailyOperationsFilter extends Filter {
    private DailyOperationTypes type;
    private String productId;
    private String serialId;
    private AggregationStatus statusNot;
    private PackageType unit;
}
