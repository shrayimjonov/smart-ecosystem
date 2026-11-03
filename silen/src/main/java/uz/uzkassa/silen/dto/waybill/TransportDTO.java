package uz.uzkassa.silen.dto.waybill;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransportDTO implements Serializable {
    protected String id;

    @JsonProperty("ProductGroups")
    protected List<ProductGroupDTO> productGroups = new ArrayList<>();

    @JsonSetter("productgroups")
    public void setproductgroupsLowerCase(List<ProductGroupDTO> productgroups) {
        this.productGroups = productgroups;
    }
}
