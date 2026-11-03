package uz.uzkassa.smartpossupply.dto.telegram;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramConnectTariffDTO {
   String tin;
   String companyName;
   String managerFullName;


    @Override
    public String toString() {
        return "🔔 Заявка на подключение услуги\n\n" +
                "🏢 Компания: " + companyName + "\n" +
                "🔢 ИНН: " + tin + "\n" +
                "👤 Менеджер: " + managerFullName;
    }
}
