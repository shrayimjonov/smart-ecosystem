package uz.uzkassa.smartpossupply.dto.genericsettings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Rayimjonov Shuxratjon
 * Date: 07.02.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileCompanySettingsListDTO implements Serializable {

    GenericSettingsType settingsType;

    boolean enabled;

    List<MobileGenericSettingsFieldDTO> properties = new ArrayList<>();
}
