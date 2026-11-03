package uz.uzkassa.smartpossupply.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransportCheckResponseDTO implements Serializable {

    private String regNo;

    private String model;

    private Integer transportType;

    private Integer ownershipType;
}