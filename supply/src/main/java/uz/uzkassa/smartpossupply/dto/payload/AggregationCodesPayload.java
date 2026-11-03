package uz.uzkassa.smartpossupply.dto.payload;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PackageType;

import java.io.Serializable;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 12.03.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationCodesPayload implements Serializable {

    List<Long> ids;

    boolean out;

    PackageType packageType;

    public AggregationCodesPayload(List<Long> ids, boolean out, PackageType packageType) {
        this.ids = ids;
        this.out = out;
        this.packageType = packageType;
    }
}
