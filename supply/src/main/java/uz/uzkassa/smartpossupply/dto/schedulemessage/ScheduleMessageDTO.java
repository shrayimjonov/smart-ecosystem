package uz.uzkassa.smartpossupply.dto.schedulemessage;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.AppCategory;
import uz.uzkassa.smartpossupply.enums.ChannelType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleMessageDTO implements Serializable {

    Long id;

    String subjectUz;

    String subjectRu;

    String subjectCyrillic;

    String messageUz;

    String messageRu;

    String messageCyrillic;

    @NotNull
    LocalDateTime scheduleDate;

    @NotNull
    Long mailingListId;

    @NotNull
    ChannelType channelType;

    @NotNull
    AppCategory appCategory;

}
