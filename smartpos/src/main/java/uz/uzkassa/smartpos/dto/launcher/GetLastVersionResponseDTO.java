package uz.uzkassa.smartpos.dto.launcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class GetLastVersionResponseDTO implements Serializable {
    @JsonProperty("version_name")
    private String versionName;
    @JsonProperty("version_code")
    private Long versionCode;
}
