package uz.uzkassa.smartpossupply.dto.sms.smartpos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmartposSmsMessageDTO implements Serializable {

    String recipient;

    @JsonProperty("message-id")
    String messageId;

    SmartposSmsDTO sms;
}
