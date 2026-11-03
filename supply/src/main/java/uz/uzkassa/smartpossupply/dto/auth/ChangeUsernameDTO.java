package uz.uzkassa.smartpossupply.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.04.2023 16:20
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeUsernameDTO implements Serializable {

    @Serial
private static final long serialVersionUID = 101242675265729737L;

    @NotNull
    @Size(min = 6, max = 6)
    String activationCode;
}
