package uz.uzkassa.smartpossupply.dto.sms.smartpos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmartposSmsRequestDTO implements Serializable {

    List<SmartposSmsMessageDTO> messages;

    public List<SmartposSmsMessageDTO> getMessages() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }
}
