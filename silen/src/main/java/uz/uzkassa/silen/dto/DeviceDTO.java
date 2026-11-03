package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Device} entity.
 */
@Getter
@Setter
@ToString
public class DeviceDTO implements Serializable {
    private String id;
    private String code;
    private String organizationId;
    private String organizationCode;
    private List<LineDTO> lines;

    private String serialNumber;
    private String stamp;
    private LocalDateTime installedDate;
    private LocalDateTime nextMaintenance;
    private String description;
    private String supplier;

}
