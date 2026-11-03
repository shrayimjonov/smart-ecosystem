package uz.uzkassa.smartpossupply.dto.genericsettings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.GenericSettingsFieldType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.ProductGroup;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Powered by: Rayimjonov Shuxratjon
 * Date: 07.02.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanySettingsListDTO implements Serializable {

    Long id;

    Long settingsId;

    GenericSettingsType settingsType;

    boolean readOnly;

    Integer orderNumber;

    Long groupId;

    String groupName;

    Integer groupOrderNumber;

    Map<GenericSettingsFieldType, GenericSettingsFieldListDTO> values = new LinkedHashMap<>();

    boolean enabled;

    String description;

    List<ProductGroup> allowedProductGroups;

    public void add(GenericSettingsFieldType key, GenericSettingsFieldListDTO value) {
        values.put(key, value);
    }
}
