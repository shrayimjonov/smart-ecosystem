package uz.uzkassa.cts.helper;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AliasFilter {
    String name() default "";
    String joinRootName() default "";
    String or() default "";
    String and() default "";
    String like() default "";
}
