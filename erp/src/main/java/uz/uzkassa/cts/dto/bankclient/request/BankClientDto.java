package uz.uzkassa.cts.dto.bankclient.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ServiceType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class BankClientDto {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    ServiceType serviceType;
    private LocalDateTime createdDate;
}
