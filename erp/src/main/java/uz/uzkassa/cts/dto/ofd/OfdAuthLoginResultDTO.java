package uz.uzkassa.cts.dto.ofd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfdAuthLoginResultDTO implements Serializable {

    String scope;

    Integer expire;

    String access_token;

    String token_type;

    String refresh_token;
}
