package uz.uzkassa.silen.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.FilterMode;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.ProductType;

import java.util.EnumSet;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DailyReportFilter extends Filter {
    String deviceId;

    String lineId;

    FilterMode filterMode = FilterMode.Production;

    ProductType productType;

    private OrganizationType organizationType;

    private EnumSet<OrganizationType> organizationTypes;

}
