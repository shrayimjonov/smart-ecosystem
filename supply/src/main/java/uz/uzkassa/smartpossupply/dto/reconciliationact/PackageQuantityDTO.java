package uz.uzkassa.smartpossupply.dto.reconciliationact;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PackageType;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PackageQuantityDTO implements Serializable {

    PackageType packageType;

    Integer quantityInPackage;

    Integer quantity;
}
