package uz.uzkassa.smartpos.dto.launcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetLastVersionRequestDTO implements Serializable {
    @NotBlank
    @JsonProperty("package_name")
    private String packageName;
    private Long versionCode;
}
