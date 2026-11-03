package uz.uzkassa.smartpossupply.dto.aggregation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PackageType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationValidationDTO implements Serializable {

    String code;

    PackageType packageType;

    boolean child;

    boolean checkAslBelgi;

    String barcode;

//    public PackageType getPackageType() {
//        if (this.packageType.equals(PackageType.BLOCK)) {
//            return PackageType.BLOCK;
//        }
//        if (this.child) {
//            return PackageType.PALLET.equals(this.packageType) ? PackageType.BOX : null;
//        }
//        return packageType;
//    }
}
