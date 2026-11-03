package uz.uzkassa.smartpossupply.dto.integration.turon.mark;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TuronUtilisationReportRequestDTO {

    String productionLineId;

    String usageType = "USED_FOR_PRODUCTION";

    Set<String> sntins = new LinkedHashSet<>();
}
