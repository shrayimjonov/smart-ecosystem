package uz.uzkassa.silen.dto.marking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Aggregation} entity.
 */
@Getter
@Setter
@ToString
public class ValidationBatchDTO implements Serializable {

    @NotBlank
    private Set<String> codes;

    @NotNull
    private PackageType unit;

    private boolean child;

    private boolean trueCheck;

    @NotNull
    private String productId;

    @NotBlank
    private String barcode;

    public PackageType getUnit() {
        if (this.unit == PackageType.BLOCK) {
            return PackageType.BLOCK;
        }
        if (this.child) {
            return PackageType.PALLET == this.unit ? PackageType.BOX : null;
        }
        return unit;
    }
}
