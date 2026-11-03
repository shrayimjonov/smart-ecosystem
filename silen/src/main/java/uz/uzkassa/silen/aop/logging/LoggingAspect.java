package uz.uzkassa.silen.aop.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.uzkassa.silen.security.SecurityUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Aspect for logging execution of service and repository Spring components.
 * <p>
 * By default, it only runs with the "dev" profile.
 */
@Aspect
@Slf4j
public class LoggingAspect {
    private final static Set<String> excludeMethods = Stream.of(
        "uz.uzkassa.silen.rest.AggregationResource",
        "uz.uzkassa.silen.rest.integration.AdminResource",
        "uz.uzkassa.silen.service.ValidationService",
        "uz.uzkassa.silen.service.AggregationService",
        "uz.uzkassa.silen.service.BaseAggregationService").collect(Collectors.toSet());


    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
        " || within(@org.springframework.stereotype.Service *)"/* +
        " || within(@org.springframework.web.bind.annotation.RestController *)"*/)
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(uz.uzkassa.silen.repository..*)" +
        " || within(uz.uzkassa.silen.service..*)"/* +
        " || within(uz.uzkassa.silen.rest..*)"*/)
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Retrieves the {@link Logger} associated to the given {@link JoinPoint}.
     *
     * @param joinPoint join point we want the logger for.
     * @return {@link Logger} associated to the given {@link JoinPoint}.
     */
    private Logger logger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice.
     * @param e         exception.
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        if ("getBalanceByCtoId".equals(joinPoint.getSignature().getName()) ||
            (excludeMethods.contains(joinPoint.getSignature().getDeclaringTypeName()) && "validate".equals(joinPoint.getSignature().getName()))
        ) {
            return;
        }
        log.error("Exception in {}.{}() with cause = '{}' and exception = '{}'", joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL", e.getMessage(), e);
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice.
     * @return result.
     * @throws Throwable throws {@link IllegalArgumentException}.
     */
    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
//        if (log.isDebugEnabled()) {
//        log.info("Enter: {}.{}()", joinPoint.getSignature().getDeclaringTypeName(),
//                joinPoint.getSignature().getName());
//        log.info("Enter: TIN {}, LOGIN {}", SecurityUtils.getCurrentOrganizationTin(), SecurityUtils.getCurrentUserLogin());
//        }
        try {
            final Object result = joinPoint.proceed();
            final long excTime = (System.currentTimeMillis() - start);
            if (excTime > 1000l/* && !excludeMethods.contains(joinPoint.getSignature().getDeclaringTypeName())*/) {
                log.info("Executed: {}ms {}.{}() [{}:{}]", excTime, joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), SecurityUtils.getCurrentOrganizationTin(), SecurityUtils.getCurrentUserLogin());
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }
}
