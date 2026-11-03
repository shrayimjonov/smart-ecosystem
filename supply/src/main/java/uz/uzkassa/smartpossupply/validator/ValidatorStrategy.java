package uz.uzkassa.smartpossupply.validator;

public interface ValidatorStrategy<T> {

    void validate(T dto);
}
