package uz.uzkassa.smartpossupply.validator;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.Locale;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LocalizationMessage {

    MessageSource messageSource;

    public String localize(String code) {
        return this.localize(code, null, code, SecurityUtils.getLocale());
    }

    public String localize(String code, Locale locale) {
        return this.localize(code, null, code, locale != null ? locale : SecurityUtils.getLocale());
    }

    public String localize(String code, Object[] params) {
        return this.localize(code, params, SecurityUtils.getLocale());
    }

    public String localize(String code, String defaultMessage) {
        return messageSource.getMessage(code, null, defaultMessage, SecurityUtils.getLocale());
    }

    public String localize(String code, Object[] params, Locale locale) {
        return this.localize(code, params, code, locale);
    }

    public String localize(String code, String... params) {
        return localize(code, params, SecurityUtils.getLocale());
    }

    public String localize(String code, Object[] params, String defaultMessage, Locale locale) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return messageSource.getMessage(code, params, defaultMessage, locale);
    }
}
