package uz.uzkassa.silen.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VatRateDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long id;

    @NotNull
    @Size(max = 20)
    String code;

    @NotNull
    @Size(max = 20)
    String name;

    BigDecimal amount;
}
