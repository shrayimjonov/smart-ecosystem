package uz.uzkassa.cts.dto.reminder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ChannelType;

import java.io.Serializable;

/**
 * Created date time: 16.01.2025 14:57
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReminderDTO implements Serializable {

    String code;

    ChannelType type;
}
