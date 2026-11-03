package uz.uzkassa.cts.helper;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface BetweenFilter {
    String name() default "";
    String joinRootName() default "";
    String from() default "";
    String to() default "";

}
