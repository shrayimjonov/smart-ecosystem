package uz.uzkassa.silen.integration.turon;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.UsageType;

import java.util.Set;

@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilisationRequest {

    UsageType usageType = UsageType.USED_FOR_PRODUCTION;

    @Size(min = 1, max = 30_000)
    Set<String> sntins;

    String productionDate;

    String expirationDate;

    @Max(value = 20, message = "Контроль длины от 1 до 20 символов")
    String seriesNumber;

    String productionLineId;

}
