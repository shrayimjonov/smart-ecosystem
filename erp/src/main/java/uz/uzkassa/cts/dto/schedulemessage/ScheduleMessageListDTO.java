package uz.uzkassa.cts.dto.schedulemessage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ChannelType;
import uz.uzkassa.cts.enums.ScheduleMessageStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleMessageListDTO implements Serializable {

    Long id;

    String subject;

    String message;

    LocalDateTime scheduleDate;

    ChannelType channelType;

    ScheduleMessageStatus status;
}
