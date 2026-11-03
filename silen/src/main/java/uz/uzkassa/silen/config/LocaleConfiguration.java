package uz.uzkassa.silen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class LocaleConfiguration {
    private final static List<Locale> locales = List.of(Locale.of("ru"), Locale.of("uz"), Locale.of("cyrl"));

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver();
        acceptHeaderLocaleResolver.setSupportedLocales(locales);
        acceptHeaderLocaleResolver.setDefaultLocale(locales.getFirst());
        return acceptHeaderLocaleResolver;
    }
}
