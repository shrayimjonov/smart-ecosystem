package uz.uzkassa.cts.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ValidationContext {

    private final Map<Class<?>, ValidatorStrategy<?>> validators = new HashMap<>();

    @Autowired
    public ValidationContext(List<ValidatorStrategy<?>> validatorStrategies) {
        validatorStrategies.forEach(strategy -> validators.put(getGenericType(strategy), strategy));
    }

    @SuppressWarnings("unchecked")
    public <T> void validate(T dto) {
        ValidatorStrategy<T> validator = (ValidatorStrategy<T>) validators.get(dto.getClass());
        if (validator == null) {
            throw new IllegalArgumentException("No validator found for " + dto.getClass().getSimpleName());
        }
        validator.validate(dto);
    }

    private Class<?> getGenericType(ValidatorStrategy<?> strategy) {
        return (Class<?>) ((ParameterizedType) strategy.getClass()
                .getGenericInterfaces()[0])
                .getActualTypeArguments()[0];
    }
}
