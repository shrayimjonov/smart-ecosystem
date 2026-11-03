package uz.uzkassa.cts.dto.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ChannelType;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReminderPayload implements Serializable {

    String subject;

    String message;

    ChannelType channelType;

    List<Long> participantIds = new ArrayList<>();

    Long companyId;

    Long entityId;

    EntityTypeEnum entityType;
}
