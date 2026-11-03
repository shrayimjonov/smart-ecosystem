package uz.uzkassa.cts.utils;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class PredicateUtil {

    public static <T> Predicate with(Supplier<T> method, Function<T, Predicate> lambda) {
        final T value = method.get();
        if (value == null)
            return null;
        if (value instanceof String && StringUtils.isEmpty((String) value)) {
            return null;
        }
        return lambda.apply(value);
    }


    public static <T> Predicate between(Supplier<T> method, Supplier<T> method1, BiFunction<T, T, Predicate> lambda) {
        final T value = method.get();
        final T value1 = method1.get();
        if (value != null && value1 != null)
            return lambda.apply(value, value1);
        return null;
    }

    public static <T> Predicate with(Supplier<T> method, Function<T, Predicate> lambdaTrue, Function<T, Predicate> lambdaFalse) {
        final T value = method.get();
        if (value != null) {
            return ((Boolean) value) ? lambdaTrue.apply(value) : lambdaFalse.apply(value);
        }
        return null;
    }
}
