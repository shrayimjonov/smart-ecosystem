package uz.uzkassa.cts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import uz.uzkassa.cts.aop.logging.LoggingAspect;
import uz.uzkassa.cts.social.TelegramService;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

    @Bean
    public LoggingAspect loggingAspect(Environment env,
                                       TelegramService telegramService) {
        return new LoggingAspect(env, telegramService);
    }
}
