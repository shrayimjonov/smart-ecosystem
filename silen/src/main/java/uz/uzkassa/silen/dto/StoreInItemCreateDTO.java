package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreInItemCreateDTO implements Serializable {

    @Size(min = 1)
    List<Long> shipmentItemIds;

    String storeInId;

}
