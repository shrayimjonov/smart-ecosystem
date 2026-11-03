package uz.uzkassa.smartpos.aop.auth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.aop.auth.annotations.AuthValidate;
import uz.uzkassa.smartpos.domain.postgres.user.OperatorPermission;
import uz.uzkassa.smartpos.enums.HeaderKeys;
import uz.uzkassa.smartpos.exceptions.AccessDeniedExp;
import uz.uzkassa.smartpos.repository.postgres.user.OperatorPermissionRepository;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.security.HeaderUtils;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Aspect
@Slf4j
@Component
public class AuthorizeAspect {

    private final OperatorPermissionRepository repository;

    @Autowired
    public AuthorizeAspect(OperatorPermissionRepository repository) {
        this.repository = repository;
    }

    @Around("@annotation(uz.uzkassa.smartpos.aop.auth.annotations.AuthValidate) && @annotation(authValidate))")
    public Object invoke(ProceedingJoinPoint pjp, AuthValidate authValidate) throws Throwable {

        List<String> authorities = getCurrentUserAuthorities();

        if (!CollectionUtils.containsAny(authorities, AuthoritiesConstants.SMARTPOS_ADMIN, AuthoritiesConstants.ADMIN, AuthoritiesConstants.SALES_MANAGER)) {
            LocalDateTime date = LocalDateTime.now();
            String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new NoSuchElementException("No userLogin present"));
            log.info("AuthorizeAspect: Login: {}", userLogin);
            String companyIdFromHeader = HeaderUtils.getCurrentHeaderByKey(HeaderKeys.COMPANY_ID);
            log.info("AuthorizeAspect: COMPANY_ID: {}", companyIdFromHeader);
            if (companyIdFromHeader == null) {
                log.error("Check Permission Exp: companyId null");
                throw new AccessDeniedExp("Access denied");
            }
            Long companyId = Long.valueOf(companyIdFromHeader);
            OperatorPermission permission = repository.findByCompanyIdAndExpireDateAfterAndUser_LoginAndActivatedIsTrue(companyId, date, userLogin);
            if (permission == null) {
                log.error("Check Permission Exp: permission is null");
                throw new AccessDeniedExp("Access denied");
            }
        }
        return pjp.proceed();
    }


    private List<String> getCurrentUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

}
