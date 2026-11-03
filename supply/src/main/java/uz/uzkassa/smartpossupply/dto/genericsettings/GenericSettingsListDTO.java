package uz.uzkassa.smartpossupply.dto.genericsettings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.ProductGroup;

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
public class GenericSettingsListDTO implements Serializable {

    Long id;

    GenericSettingsType settingsType;

    boolean readOnly;

    boolean enabled;

    boolean isCabinet;

    String description;

    List<GenericSettingsFieldListDTO> fields;

    List<ProductGroup> allowedProductGroups;
}
