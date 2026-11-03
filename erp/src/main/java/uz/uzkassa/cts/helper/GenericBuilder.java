package uz.uzkassa.cts.helper;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class GenericBuilder<T> {
    private T instance;
    private StringBuilder stringBuilder;
    private boolean ifCond = true; // default

    public GenericBuilder(Class<T> clazz) {
        try {
            instance = clazz.newInstance();
            stringBuilder = new StringBuilder();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public GenericBuilder<T> with(Consumer<T> setter) {
        System.out.println(setter);
        setter.accept(instance);
        return this;
    }

    public GenericBuilder<T> and(Consumer<T> setter) {
        stringBuilder.append(setter.getClass().getName());
        setter.accept(instance);
        return this;
    }

    public GenericBuilder<T> or(Consumer<T> setter) {
        setter.accept(instance);
        return this;
    }

    public T build() {
        return instance;
    }

    public static <T> GenericBuilder<T> of(Class<T> clazz) {
        return new GenericBuilder<>(clazz);
    }

    public GenericBuilder<T> If(BooleanSupplier condition) {
        this.ifCond = condition.getAsBoolean();
        return this;
    }

    public GenericBuilder<T> endIf() {
        this.ifCond = true;
        return this;
    }

}
