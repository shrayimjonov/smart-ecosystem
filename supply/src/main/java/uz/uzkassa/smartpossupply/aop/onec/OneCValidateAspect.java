package uz.uzkassa.smartpossupply.aop.onec;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.service.CompanySettingsService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OneCValidateAspect {

    CompanySettingsService companySettingsService;

    /**
     * Executes before any method in a class annotated with @OneCValidate
     */

    @Before("@within(oneCValidate)")
    public void validateCompanyAccess(JoinPoint joinPoint, OneCValidate oneCValidate) {
        boolean isEnabled = companySettingsService.is1CSyncEnabled(SecurityUtils.getCompanyId());

        if (!isEnabled) {
            throw new BadRequestException("Срок действия оплаты за интеграцию с 1С истёк или услуга отключена. Пожалуйста, обратитесь в службу поддержки.");
        }

    }
}
