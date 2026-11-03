package uz.uzkassa.horeca.dto.sms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class EtcEntityDTO implements Serializable {

    String login;
    String pwd;

    @JsonProperty("CgPN")
    String cgPN;

    @JsonProperty("CdPN")
    String cdPN;

    String text;
}
