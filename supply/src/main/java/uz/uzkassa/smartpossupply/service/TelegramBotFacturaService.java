package uz.uzkassa.smartpossupply.service;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;

@Component
public class TelegramBotFacturaService extends TelegramBotBaseService {

    TelegramBot telegramBot;

    public TelegramBotFacturaService(ApplicationProperties applicationProperties, @Qualifier("telegramFacturaBot") TelegramBot telegramBot) {
        super(applicationProperties);
        this.telegramBot = telegramBot;
    }

    public void send(String message) {
        super.send(message, applicationProperties.getTelegramBotFacturaConfig().getChannelId(), telegramBot);
    }
}
