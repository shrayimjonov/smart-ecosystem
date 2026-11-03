package uz.uzkassa.smartpossupply.dto.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EtcEntityDTO {

    String login;

    String pwd;

    @JsonProperty("CgPN")
    String cgPN;

    @JsonProperty("CdPN")
    String cdPN;

    String text;
}
