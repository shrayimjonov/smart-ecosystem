package uz.uzkassa.silen.dto.marking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreInMarkValidationDTO implements Serializable {

    @NotBlank
    String code;

    @NotNull
    PackageType unit;

    boolean child;

    boolean trueCheck;

    @NotNull
    Long itemId;

    @NotBlank
    String storeInId;

    public PackageType getUnit() {
        if (this.unit.equals(PackageType.BLOCK)) {
            return PackageType.BLOCK;
        }
        if (this.child) {
            return PackageType.PALLET.equals(this.unit) ? PackageType.BOX : null;
        }
        return unit;
    }
}
