package uz.uzkassa.silen.dto.waybill;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Carriages implements Serializable {
    @JsonProperty("Carriages")
    List<TruckDTO> truckDTO;

    @JsonSetter("settruck")
    public void settruck(List<TruckDTO> truckDTO) {
        this.truckDTO = truckDTO;
    }
}
