package uz.uzkassa.smartpossupply.dto.aslbelgi.aic.xtrace;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneratedAicResponse implements Serializable {

    String aggregatedCustomsCode;
}
