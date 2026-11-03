package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApayCardPanDTO implements Serializable {

    @NonNull
    @NotEmpty
    String pan;
}
