package uz.uzkassa.silen.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrganizationEquipmentsDTO {

    String id;

    String deviceId;

    String deviceName;

    @NotNull
    String lineId;

    String lineName;

    @NotNull
    String equipmentId;
    EquipmentDTO equipment;

    String idEquipment;

    @NotNull
    String organizationId;

    String organizationName;

    Integer countInstalled;

    String description;
}
