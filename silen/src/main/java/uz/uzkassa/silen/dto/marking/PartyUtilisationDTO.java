package uz.uzkassa.silen.dto.marking;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.UsageType;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartyUtilisationDTO {

    UsageType usageType = UsageType.USED_FOR_PRODUCTION;

    String productionDate;

    String expirationDate;

    @Size(max = 20, message = "Контроль длины от 1 до 20 символов")
    String seriesNumber;

}
