package uz.uzkassa.cts.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecretKeyDTO implements Serializable {

    @NotNull
    @Size(min = 36, max = 36)
    String secretKey;

    public SecretKeyDTO(String secretKey) {
        this.secretKey = secretKey;
    }
}
