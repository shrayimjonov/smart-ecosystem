package uz.uzkassa.cts.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerLookUpDTO implements Serializable {

    Long id;
    String name;
    String tin;
    String appType;
    Long vatId;

    public CustomerLookUpDTO(Long id, String name, String tin) {
        this.id = id;
        this.name = name;
        this.tin = tin;
    }
    public CustomerLookUpDTO(Long id, String name, String tin, String appType, Long vatId) {
        this.id = id;
        this.name = name;
        this.tin = tin;
        this.appType = appType;
        this.vatId = vatId;
    }

}
