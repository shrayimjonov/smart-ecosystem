package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.Locale;


@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class LocalizationService {

    @NonNull
    MessageSource messageSource;

    public String localize(String code) {
        return this.localize(code, null, code, HeaderUtils.getLocale());
    }

    public String localize(String code, Object[] params) {
        return this.localize(code, params, HeaderUtils.getLocale());
    }

    public String localize(String code, String defaultMessage) {
        return messageSource.getMessage(code, null, defaultMessage, HeaderUtils.getLocale());
    }

    public String localize(String code, Object[] params, Locale locale) {
        return this.localize(code, params, code, locale);
    }

    public String localize(String code, Object[] params, String defaultMessage, Locale locale) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return messageSource.getMessage(code, params, defaultMessage, locale);
    }
}
