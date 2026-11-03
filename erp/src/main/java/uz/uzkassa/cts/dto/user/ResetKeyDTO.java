package uz.uzkassa.cts.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResetKeyDTO implements Serializable {

    @NotNull
    @NotEmpty
    private String phone;

    @NotNull
    @NotEmpty
    private String key;
}


