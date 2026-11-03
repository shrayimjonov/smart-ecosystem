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
@FieldDefaults(level = AccessLevel.PROTECTED)
public class RefreshTokenDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 456467L;

    @JsonProperty("refresh_token")
    String refreshToken;
}
