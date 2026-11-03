package uz.uzkassa.cts.validator;

public interface ValidatorStrategy<T> {

    void validate(T dto);
}
