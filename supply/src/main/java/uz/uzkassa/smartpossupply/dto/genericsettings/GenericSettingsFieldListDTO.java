package uz.uzkassa.smartpossupply.dto.genericsettings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.GenericSettingsFieldType;
import uz.uzkassa.smartpossupply.enums.ValueType;
import uz.uzkassa.smartpossupply.enums.WidgetType;

import java.io.Serializable;
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
public class GenericSettingsFieldListDTO implements Serializable {

    GenericSettingsFieldType fieldType;

    WidgetType widgetType;

    ValueType valueType;

    String defaultValue;

    String format;

    Object value;

    int maxSize;

    String placeholder;

    List<Map<String, String>> options;
}
