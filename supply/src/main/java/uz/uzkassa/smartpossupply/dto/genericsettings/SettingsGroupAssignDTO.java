package uz.uzkassa.smartpossupply.dto.genericsettings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettingsGroupAssignDTO implements Serializable {

    Long settingsId;

    Long settingsGroupId;
}
