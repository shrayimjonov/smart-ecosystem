package uz.uzkassa.cts.helper.excelhelper.annotaion;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XlsxSheet {
    String value();
}
