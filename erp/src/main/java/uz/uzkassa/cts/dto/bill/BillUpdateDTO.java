package uz.uzkassa.cts.dto.bill;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillUpdateDTO implements Serializable {

    Long id;
    @NotNull
    @NotEmpty
    String billId;
    @NotNull
    @NotEmpty
    String pan;
    @NotNull
    @NotEmpty
    String expiry;
}
