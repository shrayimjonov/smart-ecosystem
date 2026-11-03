package uz.uzkassa.smartpos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import uz.uzkassa.smartpos.aop.logging.LoggingAspect;

@Configuration(proxyBeanMethods = false)
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

    @Bean
//    @Profile(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
}
