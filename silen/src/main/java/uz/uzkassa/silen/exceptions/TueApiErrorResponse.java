package uz.uzkassa.silen.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TueApiErrorResponse {
    @JsonProperty("error_message")
    private String errorMessage;

    @Override
    public String toString() {
        return "Ошибка от TrueApi!\n" + errorMessage;
    }
}
