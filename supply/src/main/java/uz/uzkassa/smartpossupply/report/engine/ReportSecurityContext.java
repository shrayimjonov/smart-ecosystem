package uz.uzkassa.smartpossupply.report.engine;

import uz.uzkassa.smartpossupply.enums.LanguageEnum;

public record ReportSecurityContext(
        Long userId,
        Long companyId,
        LanguageEnum language
) {

    public LanguageEnum effectiveLanguage() {
        return language == null ? LanguageEnum.getDefaultLanguage() : language;
    }
}
