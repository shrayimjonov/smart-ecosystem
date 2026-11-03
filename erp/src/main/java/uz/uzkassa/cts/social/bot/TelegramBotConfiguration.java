package uz.uzkassa.cts.social.bot;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uz.uzkassa.cts.config.ApplicationProperties;

@Configuration
public class TelegramBotConfiguration {
    @Bean
    public TelegramBot telegramBot(ApplicationProperties applicationProperties) {
        return new TelegramBot.Builder(applicationProperties.getTelegramBotCaseProperty().getToken())
                .apiUrl("http://127.0.0.1:8080/bot")
                .build();
    }
}
