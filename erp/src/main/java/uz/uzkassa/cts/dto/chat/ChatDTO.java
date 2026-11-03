package uz.uzkassa.cts.dto.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serializable;
import java.util.List;

/**
 * Created date time: 13.02.2025 12:13
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatDTO implements Serializable {

    Long entityId;

    EntityTypeEnum entityType;

    String message;

    Long attachmentId;

    List<Long> memberIds;
}
