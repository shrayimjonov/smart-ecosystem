package uz.uzkassa.cts.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerResponseDTO implements Serializable {

    boolean bankTariff = false;
    boolean rentDevice = false;
}
