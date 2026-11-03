package uz.uzkassa.smartpossupply.dto.schedulemessage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.ScheduleMessageStatus;

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

    ScheduleMessageStatus status;

    String subjectUz;

    String subjectRu;

    String subjectCyrillic;

    String messageUz;

    String messageRu;

    String messageCyrillic;
}
