package uz.uzkassa.smartpossupply.dto.genericsettings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

/**
 * Powered by: Rayimjonov Shuxratjon
 * Date: 07.02.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenericSettingsDTO implements Serializable {

    String settingsType;

    List<GenericSettingsFieldDTO> fields;

    GenericSettingsDescriptionDTO description;

    boolean readOnly;

    boolean isCabinet=true;

    List<String> allowedProductGroups;
}
