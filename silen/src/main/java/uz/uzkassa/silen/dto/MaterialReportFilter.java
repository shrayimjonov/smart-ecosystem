package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.enumeration.PackageType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialReportFilter extends Filter {

    String productId;

    Set<PackageType> packageTypes = new HashSet<>();

    LocalDateTime from = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

    LocalDateTime to = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
}
