package uz.uzkassa.smartpos.config;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
public class TelegramBotConfig {
    @Bean
    @Primary
    public TelegramBot clientBot(ApplicationProperties applicationProperties) {
        return new TelegramBot.Builder(applicationProperties.getClientBotToken())
                .apiUrl("http://127.0.0.1:8080/bot")
                .build();
    }

    @Bean
    public TelegramBot logBot(ApplicationProperties applicationProperties) {
        return new TelegramBot.Builder(applicationProperties.getLogBotToken())
                .apiUrl("http://127.0.0.1:8080/bot")
                .build();
    }
}
