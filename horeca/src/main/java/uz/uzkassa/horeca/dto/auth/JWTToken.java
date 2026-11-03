package uz.uzkassa.horeca.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JWTToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1452553L;

    @JsonProperty("id_token")
    String idToken;
}
