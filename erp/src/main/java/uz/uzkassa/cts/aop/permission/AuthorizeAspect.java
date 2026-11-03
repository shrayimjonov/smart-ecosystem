package uz.uzkassa.cts.aop.permission;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.web.rest.errors.ForbiddenException;

import java.util.Objects;

@Aspect
@Slf4j
@Component
public class AuthorizeAspect {

    @Around("@annotation(uz.uzkassa.cts.aop.permission.Authorize) && @annotation(authorize)")
    public Object invoke(ProceedingJoinPoint pjp, Authorize authorize) throws ForbiddenException {

        Object result;
        if (SecurityUtils.isCurrentUserInRole(Role.ADMIN.getRoleCode())) {
            try {
                result = pjp.proceed();
            } catch (Throwable throwable) {
                log.error("Check Permission Exp: {}", throwable.getMessage());
                throw new ForbiddenException("Access denied");
            }
            return result;
        }
        if (StringUtils.isNotEmpty(authorize.name()) && hasPermission(authorize.name())) {
            try {
                pjp.proceed();
            } catch (Throwable throwable) {
                log.error("Check Permission Exp: {}", throwable.getMessage());
                throw new ForbiddenException("Access denied");
            }
        }
        throw new ForbiddenException("Access denied");
    }

    private static boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(m -> Objects.equals(m.getAuthority(), permission));
    }

}
