package uz.uzkassa.smartpossupply.dto.telegramintegration;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.TelegramBotType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramIntegrationDTO implements Serializable {

    Long id;

    @NotNull
    String botToken;

    @NotNull
    String botUsername;

    @NotNull
    Long companyId;

    @NotNull
    TelegramBotType botType;
}
