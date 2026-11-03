package uz.uzkassa.cts.criteriahelper;


import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AliasMap {
    String name() default "";
    String joinRootName() default "";
}
