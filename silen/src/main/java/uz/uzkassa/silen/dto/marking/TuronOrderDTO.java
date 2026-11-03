package uz.uzkassa.silen.dto.marking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.CreateMethodType;
import uz.uzkassa.silen.enumeration.ReleaseMethodType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Order} entity.
 * <p>
 * http://<server-name>[:server-port]/api/v2/{tobacco}/orders?omsId={omsId}
 * Header clientToken:{clientToken}
 * {
 *     "releaseMethodType": "PRODUCTION",
 *     "createMethodType": "SELF_MADE",
 *     "products": [
 *         {
 *             "templateId": 3,
 *             "gtin": "04870216762506",
 *             "quantity": 1,
 *             "serialNumberType": "SELF_MADE"
 *         }
 *     ],
 *     "factoryId": "Identasdifier",
 *     "factoryCountry": "asda",
 *     "productCode": "111",
 *     "productDescription": "Simple ",
 *     "productionLineId": "1"
 * }
 * </p>
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TuronOrderDTO implements Serializable {
    @NotNull
    String contactPerson;

    ReleaseMethodType releaseMethodType;

    CreateMethodType createMethodType;

    @NotNull
    Set<TuronOrderProductDTO> products = new HashSet<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String factoryId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String factoryCountry;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String productionLineId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String productCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String productDescription;

    @JsonIgnore
    public void addProduct(TuronOrderProductDTO turonOrderProductDTO) {
        this.products.add(turonOrderProductDTO);
    }
}
