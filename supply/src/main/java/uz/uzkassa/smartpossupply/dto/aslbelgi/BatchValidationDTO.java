package uz.uzkassa.smartpossupply.dto.aslbelgi;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "token")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BatchValidationDTO implements Serializable {

    Set<String> aggregationCodes = new LinkedHashSet<>();

    Set<String> markCodes = new LinkedHashSet<>();

    Set<String> aggregationBlockCodes = new LinkedHashSet<>();

    @NotNull
    ProductType productType;

    String barcode;

    Long productId;

    @Deprecated
    String parentBarcode;

    String token;

    boolean trueCheck;

    Long companyCommittentId;

    String boxBarcode;

    String blockBarcode;

    public List<String> getAggregationCodes() {
        return new ArrayList<>(this.aggregationCodes);
    }

    public List<String> getMarkCodes() {
        return new ArrayList<>(this.markCodes);
    }

    public List<String> getAggregationBlockCodes() {
        return new ArrayList<>(this.aggregationBlockCodes);
    }
}
