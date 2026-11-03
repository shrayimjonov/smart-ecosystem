package uz.uzkassa.horeca.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenDTO extends RefreshTokenDTO {

    @Serial
    private static final long serialVersionUID = 5545453L;

    @JsonProperty("access_token")
    String accessToken;

    public TokenDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public TokenDTO(String accessToken, String refreshToken) {
        super(refreshToken);
        this.accessToken = accessToken;
    }
}
