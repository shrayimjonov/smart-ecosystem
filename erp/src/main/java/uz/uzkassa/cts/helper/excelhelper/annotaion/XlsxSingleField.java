package uz.uzkassa.cts.helper.excelhelper.annotaion;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XlsxSingleField {
    int columnIndex();
    String columnName() default "";
}
