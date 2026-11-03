package uz.uzkassa.smartpossupply.dto.ofd;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfdAuthLoginResultDTO implements Serializable {

    String scope;

    Integer expire;

    String access_token;

    String token_type;

    String refresh_token;
}
