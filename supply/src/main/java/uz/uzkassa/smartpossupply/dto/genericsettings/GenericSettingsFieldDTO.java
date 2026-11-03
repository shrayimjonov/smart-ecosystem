package uz.uzkassa.smartpossupply.dto.genericsettings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericSettingsFieldDTO implements Serializable {

    String fieldType;

    String widgetType;

    String valueType;

    String defaultValue;

    String format;

    int maxSize;

    List<String> options;

    String placeholder;
}
