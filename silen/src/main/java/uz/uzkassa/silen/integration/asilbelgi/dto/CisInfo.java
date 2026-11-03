package uz.uzkassa.silen.integration.asilbelgi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.MarkStatus;
import uz.uzkassa.silen.integration.asilbelgi.enums.MarkUnit;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CisInfo implements Serializable {
    private String requestedCis;
    private String cis;
    private String gtin;
    private String productName;
    private Integer productGroupId;
    private String productGroup;
    private String brand;
    private LocalDateTime producedDate;
    private LocalDateTime productionDate;
    private LocalDateTime expirationDate;
    private String packageTypeName;
    private MarkUnit packageType;
    private String ownerInn;
    private String ownerName;
    private MarkStatus status;
    private String statusName;
    private String parent;
    private String producerInn;
    private String producerName;
    private String productionSerialNumber;
    private Set<String> child;

    public void setStatus(MarkStatus status) {
        this.status = status;
        this.statusName = status.getText();
    }
}
