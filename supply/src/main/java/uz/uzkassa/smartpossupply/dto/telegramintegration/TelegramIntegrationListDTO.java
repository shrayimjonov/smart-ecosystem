package uz.uzkassa.smartpossupply.dto.telegramintegration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.TelegramBotType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramIntegrationListDTO implements Serializable {

    Long id;

    String botToken;

    String botUsername;

    CompanyBaseDTO company;

    TelegramBotType botType;
}
