package uz.uzkassa.smartpossupply.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.04.2023 17:02
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecretKeyDTO {

    @NotNull
    @NotEmpty
    @Size(min = 36, max = 36)
    String secretKey;

    public SecretKeyDTO(String secretKey) {
        this.secretKey = secretKey;
    }
}
