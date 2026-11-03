package uz.uzkassa.smartpos.integration.apay.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ApayCardPanDTO implements Serializable {

    @NonNull
    @NotEmpty
    private String pan;
}
