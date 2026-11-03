package uz.uzkassa.smartpossupply.config;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TelegramBotConfiguration {

    private final ApplicationProperties applicationProperties;

    public TelegramBotConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean(name = "telegramApplicationLogBot")
    public TelegramBot telegramApplicationLogBot() {
        return new TelegramBot.Builder(applicationProperties.getTelegramBotApplicationLogConfig().getToken())
                .apiUrl(applicationProperties.getTelegramBotApplicationLogConfig().getProxyUrl())
                .build();
    }

    @Bean(name = "telegramFacturaBot")
    public TelegramBot telegramFacturaBot() {
        return new TelegramBot.Builder(applicationProperties.getTelegramBotFacturaConfig().getToken())
                .apiUrl(applicationProperties.getTelegramBotFacturaConfig().getProxyUrl())
                .build();
    }

    @Bean(name = "telegramConnectTariffBot")
    public TelegramBot telegramConnectTariffBot() {
        return new TelegramBot.Builder(applicationProperties.getTelegramConnectTariffConfig().getToken())
                .apiUrl(applicationProperties.getTelegramConnectTariffConfig().getProxyUrl())
                .build();
    }
}
