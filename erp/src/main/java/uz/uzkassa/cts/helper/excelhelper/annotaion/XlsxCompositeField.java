package uz.uzkassa.cts.helper.excelhelper.annotaion;

import java.lang.annotation.*;

/*
USING EXAMPLE
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@XlsxSheet(value = "Users")
public class XlsxUser {

    @XlsxSingleField(columnIndex = 0)
    private String name;
    @XlsxSingleField(columnIndex = 1)
    private String gender;
    @XlsxSingleField(columnIndex = 2)
    private Integer age;
    @XlsxSingleField(columnIndex = 3)
    private Double bmiValue;
    @XlsxSingleField(columnIndex = 4)
    private Boolean isOverweight;
    @XlsxSingleField(columnIndex = 5)
    private List<String> activities;
    @XlsxCompositeField(from = 6, to = 7)
    private List<XlsxDietPlan> plans;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    public static class XlsxDietPlan {
        @XlsxSingleField(columnIndex = 6)
        private String mealName;
        @XlsxSingleField(columnIndex = 7)
        private Double calories;
    }

}*/

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XlsxCompositeField {
    int from();
    int to();
    String columnName() default "";
}
