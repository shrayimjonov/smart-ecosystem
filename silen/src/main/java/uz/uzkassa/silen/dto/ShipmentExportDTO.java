package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.enumeration.FileType;

import java.util.Set;

@Getter
@Setter
public class ShipmentExportDTO extends Filter {
    @Size(min = 1, max = 100)
    private Set<Long> shipmentItems;
    private FileType fileType;

    public ShipmentExportDTO() {
    }


    public ShipmentExportDTO(Set<Long> shipmentItems, FileType fileType) {
        this.shipmentItems = shipmentItems;
        this.fileType = fileType;
    }
}
