package uz.uzkassa.cts.dto.bill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class BillRequestDTO implements Serializable {

    Long id;

    String billId;

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 16, max = 16)
    private String pan;

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 4, max = 4)
    private String expiry;

    @NotNull
    private BigDecimal amount;
}
