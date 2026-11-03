package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaErrorResponse {
    @JsonProperty("ErrorCode")
    private int errorCode;
    @JsonProperty("ErrorId")
    private String errorId;
    @JsonProperty("NameRu")
    private String nameRu;
    @JsonProperty("NameUz")
    private String nameUz;
    @JsonProperty("ErrorMessage")
    private String errorMessage;
}
