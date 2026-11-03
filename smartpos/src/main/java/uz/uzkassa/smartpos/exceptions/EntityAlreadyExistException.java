package uz.uzkassa.smartpos.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityAlreadyExistException extends RuntimeException {

    String title;

    public EntityAlreadyExistException(String message) {
        super(message);
    }

}
