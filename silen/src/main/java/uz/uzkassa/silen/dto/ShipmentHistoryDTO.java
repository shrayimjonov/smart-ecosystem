package uz.uzkassa.silen.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShipmentHistoryDTO implements Serializable {

    SelectItem shipment;

    List<SelectItem> invoice;
}
