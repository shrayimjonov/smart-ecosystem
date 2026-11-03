package uz.uzkassa.smartpossupply.dto.genericsettings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminGenericSettingsGroupDTO implements Serializable {

    Long id;
    String name;
    Long order;

}
