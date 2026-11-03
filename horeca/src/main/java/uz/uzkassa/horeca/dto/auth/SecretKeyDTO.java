package uz.uzkassa.horeca.dto.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecretKeyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1022426715809722737L;

    String secretKey;
}
