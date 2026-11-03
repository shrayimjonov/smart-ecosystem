package uz.uzkassa.smartpossupply.service;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;

@Component
@Slf4j
public class TelegramBotApplicationLogService extends TelegramBotBaseService {

    TelegramBot telegramBot;

    public TelegramBotApplicationLogService(ApplicationProperties applicationProperties, @Qualifier("telegramApplicationLogBot") TelegramBot telegramBot) {
        super(applicationProperties);
        this.telegramBot = telegramBot;
    }

    public void send(String message) {
        super.send(message, applicationProperties.getTelegramBotApplicationLogConfig().getChannelId(), telegramBot);
    }
}
