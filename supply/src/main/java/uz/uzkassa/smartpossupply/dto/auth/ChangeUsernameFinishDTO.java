package uz.uzkassa.smartpossupply.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.04.2023 16:20
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeUsernameFinishDTO extends PhoneDTO {

    @Serial
private static final long serialVersionUID = 101242675229737L;

    @NotNull
    @Size(min = 36, max = 36)
    String secretKey;
}
