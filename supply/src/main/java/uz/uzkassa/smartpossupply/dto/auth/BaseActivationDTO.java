package uz.uzkassa.smartpossupply.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseActivationDTO implements Serializable {

    @Serial
private static final long serialVersionUID = 865728737L;

    @NotNull
    @Size(min = 6, max = 6)
    String activationCode;
}
