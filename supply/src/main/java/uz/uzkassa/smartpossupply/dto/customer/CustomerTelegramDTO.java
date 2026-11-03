package uz.uzkassa.smartpossupply.dto.customer;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.enums.TelegramBotType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerTelegramDTO {

    @NotNull
    Long userId;

    @NotNull
    String token;

    @NotNull
    String name;

    String languageCode;

    @NotNull
    String phone;

    TelegramBotType botType;

}
