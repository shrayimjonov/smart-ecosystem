package uz.uzkassa.silen.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionDTO {

    Long id;

    @Size(min = 1)
    int major;

    @Size(min = 1)
    int minor;

    @Size(min = 1)
    int patch;

    boolean active = false;
}
