package uz.uzkassa.silen.dto.marking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ValidationDTO implements Serializable {
    @NotBlank
    private String code;
    @NotNull
    private PackageType unit;
    private boolean child;
    private boolean trueCheck;
    @NotNull
    private String productId;
    @NotBlank
    private String barcode;

//    public PackageType getUnit() {
//        if (this.unit == PackageType.BLOCK) {
//            return PackageType.BLOCK;
//        }
//        if (this.child) {
//            return PackageType.PALLET == this.unit ? PackageType.BOX : null;
//        }
//        return unit;
//    }
}
