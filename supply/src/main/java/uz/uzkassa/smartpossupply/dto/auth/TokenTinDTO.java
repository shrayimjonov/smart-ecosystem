package uz.uzkassa.smartpossupply.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenTinDTO implements Serializable {

    @JsonProperty("access_token")
    String accessToken;

    String tin;
}
