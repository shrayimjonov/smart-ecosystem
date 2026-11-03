package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InstalledEquipmentsDTO {
    String kind;
    String type;
    String brand;
    String model;
    LocalDateTime createdDate;
    Integer installed;

    public InstalledEquipmentsDTO(String kind, String typ, String brand, String model, LocalDateTime createdDate, Integer installed) {
        this.kind = kind;
        this.type = typ;
        this.brand = brand;
        this.model = model;
        this.createdDate = createdDate;
        this.installed = installed;
    }
}
