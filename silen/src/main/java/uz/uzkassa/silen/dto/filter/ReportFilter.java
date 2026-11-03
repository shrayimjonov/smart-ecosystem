package uz.uzkassa.silen.dto.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.ReportStatus;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportFilter extends Filter {

    OrganizationType organizationType;

    Long reportId;

    @ToString.Include
    private String productId;

    @ToString.Include
    private String productSerial;

    ReportStatus status;
}
