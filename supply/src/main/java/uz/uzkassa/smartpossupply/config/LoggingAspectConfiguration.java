package uz.uzkassa.smartpossupply.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import uz.uzkassa.smartpossupply.aop.logging.LoggingAspect;

@Configuration(proxyBeanMethods = false)
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
}
