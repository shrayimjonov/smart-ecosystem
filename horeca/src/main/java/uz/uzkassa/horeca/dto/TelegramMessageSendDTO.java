package uz.uzkassa.horeca.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TelegramMessageSendDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 133898928L;

    @JsonProperty("channelId")
    String channelId;

    @JsonProperty("message")
    String message;
}
