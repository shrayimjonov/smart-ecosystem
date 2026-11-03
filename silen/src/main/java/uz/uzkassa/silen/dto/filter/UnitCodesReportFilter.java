package uz.uzkassa.silen.dto.filter;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.AggregationType;
import uz.uzkassa.silen.enumeration.CodeLength;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnitCodesReportFilter extends Filter implements Serializable {

    String productId;

    String serialId;

    AggregationStatus aggregationStatus;

    PackageType unit;

    String login;

    AggregationType type;

    String partyAggregationId;

}
