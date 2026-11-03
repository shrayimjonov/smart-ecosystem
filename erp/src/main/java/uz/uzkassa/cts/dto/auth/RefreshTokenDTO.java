package uz.uzkassa.cts.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenDTO implements Serializable {

    @JsonProperty("refresh_token")
    String refreshToken;

    @JsonProperty("access_token")
    String accessToken;

}
