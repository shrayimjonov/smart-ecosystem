package uz.uzkassa.smartpossupply.aop.auth;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AuthValidate {
    String name() default "";

    AuthEnum authEnum() default AuthEnum.COMPANY_ID;
}
