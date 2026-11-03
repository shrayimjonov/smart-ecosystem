package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class WebhookSignDTO implements Serializable {
    @JsonProperty("Sign")
    private String sign;
}
