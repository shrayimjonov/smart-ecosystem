package uz.uzkassa.smartpos.dto.nic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ErrorResponseDTO implements Serializable {
    @JsonProperty("ErrorId")
    private String errorId;
    @JsonProperty("NameRu")
    private String nameRu;
    @JsonProperty("NameUz")
    private String nameUz;
    @JsonProperty("ErrorMessage")
    private String errorMessage;
}
