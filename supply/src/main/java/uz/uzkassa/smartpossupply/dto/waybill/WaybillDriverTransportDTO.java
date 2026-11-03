package uz.uzkassa.smartpossupply.dto.waybill;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class WaybillDriverTransportDTO implements Serializable {
    private String regNo;
    private String model;
    private Integer ownershipType;
    private Integer transportType;
}
