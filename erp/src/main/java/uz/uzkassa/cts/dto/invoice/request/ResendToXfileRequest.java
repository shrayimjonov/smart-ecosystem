package uz.uzkassa.cts.dto.invoice.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class ResendToXfileRequest implements Serializable {
    @NotNull
    private Long id;
    @NotNull
    private LocalDate date;
}
