package uz.uzkassa.cts.dto.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EtcEntityDTO {
    private String login;
    private String pwd;
    @JsonProperty("CgPN")
    private String cgPN;
    @JsonProperty("CdPN")
    private String cdPN;
    private String text;
}
