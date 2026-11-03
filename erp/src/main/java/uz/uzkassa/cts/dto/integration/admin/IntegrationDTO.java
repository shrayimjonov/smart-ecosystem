package uz.uzkassa.cts.dto.integration.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.IntegrationType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntegrationDTO extends CommonDTO implements Serializable {

    String description;

    IntegrationType type;
}
