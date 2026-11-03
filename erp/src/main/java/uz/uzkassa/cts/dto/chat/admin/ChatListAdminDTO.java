package uz.uzkassa.cts.dto.chat.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.file.FileDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created date time: 13.02.2025 16:43
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatListAdminDTO implements Serializable {

    Long id;

    String message;

    FileDTO attachment;

    CommonDTO creator;

    LocalDateTime createdDate;
}
