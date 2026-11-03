package uz.uzkassa.smartpos.integration.ofd;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class OfdErrorDTO implements Serializable {
    private String field;
    private String message;
}
