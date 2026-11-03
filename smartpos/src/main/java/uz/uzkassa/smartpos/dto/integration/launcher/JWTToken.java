package uz.uzkassa.smartpos.dto.integration.launcher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWTToken {
    private String idToken;

    @JsonCreator
    public JWTToken(@JsonProperty("id_token") String idToken) {
        this.idToken = idToken;
    }

    @JsonProperty("id_token")
    public String getIdToken() {
        return idToken;
    }
}
