package uz.uzkassa.smartpossupply.dto.waybill;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpossupply.enums.TransportType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaybillRailwayTransportDTO implements Serializable {
    private Long id;

    @JsonProperty("TrainCarriageNumber")
    private String trainCarriageNumber;

    @JsonProperty("TrainDirection")
    private String trainDirection;

    @JsonProperty("ProductGroups")
    private List<WaybillProductGroupDTO> productGroups;

    private TransportType transportType;

    @JsonSetter("productgroups")
    public void setproductgroupsToLowerCase(List<WaybillProductGroupDTO> productgroups) {
        this.productGroups = productgroups;
    }

    @JsonIgnore
    public void addProductGroup(WaybillProductGroupDTO productGroupDTO) {
        if (this.productGroups == null) {
            this.productGroups = new ArrayList<>();
        }
        productGroups.add(productGroupDTO);
    }
}
