package uz.uzkassa.smartpossupply.dto.integration.turon.mark;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TuronMarkOrderDTO implements Serializable {

    String releaseMethodType;

    String createMethodType;

    String contactPerson;

    String factoryId;

    String factoryCountry;

    String productionLineId;

    String productCode;

    String productDescription;

    List<TuronMarkOrderProductDTO> products = new ArrayList<>();

    @JsonIgnore
    public void addProduct(TuronMarkOrderProductDTO turonMarkOrderProductDTO) {
        products.add(turonMarkOrderProductDTO);
    }
}
