package uz.uzkassa.cts.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Builder<T> {
    // A Supplier is a functional interface that takes no arguments and returns a value.
    // In this case, the Supplier is used to create a new instance of a generic type T.
    private final Supplier<T> instantiator;

    // A List of Consumer functions, where each Consumer modifies a T instance in some way.
    private final List<Consumer<T>> instanceModifiers = new ArrayList<>();

    // Constructor that takes a Supplier of T instances
    public Builder(Supplier<T> instantiator) {
        this.instantiator = instantiator;
    }

    // Static factory method that creates a new Builder with the given instantiator
    public static <T> Builder<T> of(Supplier<T> instantiator) {
        return new Builder<>(instantiator);
    }

    // Method that takes a BiConsumer and a value of type U, and adds a new Consumer to the list
    // of instanceModifiers. The new Consumer uses the BiConsumer to modify a T instance with the
    // given value.
    public <U> Builder<T> with(BiConsumer<T, U> consumer, U value) {
        Consumer<T> c = instance -> consumer.accept(instance, value);
        instanceModifiers.add(c);
        return this;
    }

    // Method that creates a new instance of T using the instantiator, applies all the instanceModifiers
    // to it, and returns the result.
    public T build() {
        T value = instantiator.get();
        instanceModifiers.forEach(modifier -> modifier.accept(value));
        instanceModifiers.clear();
        return value;
    }


}
