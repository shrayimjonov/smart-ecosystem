package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DropoutValidateDTO {

    @NonNull
    String printCode;
}
