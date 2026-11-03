package uz.uzkassa.smartpos.dto.etcsms.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EtcEntityDto {
    private String login;
    private String pwd;
    @JsonProperty("CgPN")
    private String cgPN;
    @JsonProperty("CdPN")
    private String cdPN;
    private String text;
}
