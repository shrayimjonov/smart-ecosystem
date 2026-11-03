package uz.uzkassa.smartpossupply;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
@ConfigurationPropertiesScan("uz.uzkassa.smartpossupply.property")
@Slf4j
public class SmartpossupplyApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SmartpossupplyApp.class, args);
        Environment env = applicationContext.getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        log.info("""
                        \n----------------------------------------------------------
                        \tApplication name :{}
                        \tSwagger URL: {}
                        \tJava version: {}
                        \tTimezone: {}
                        \tProfile(s): \t{}
                        ----------------------------------------------------------""",
                env.getProperty("spring.application.name"),
                env.getProperty("application.serverUrl") + "/swagger-ui/index.html",
                env.getProperty("java.version"),
                env.getProperty("user.timezone"),
                env.getActiveProfiles());
    }
}
