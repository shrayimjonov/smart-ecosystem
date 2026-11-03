package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class EquipmentDTO implements Serializable {
    private String id;
    private String kindId;
    private String kindName;
    private String typeId;
    private String typeName;
    private String brandId;
    private String brandName;
    private String modelId;
    private String modelName;

    private String name;
    private String organizationName;
    private LocalDateTime installedDate;
    private LocalDateTime lastServiceDate;
    private String IdEquipment;
}
