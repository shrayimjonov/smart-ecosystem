package uz.uzkassa.cts.dto.rentactaccessory.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class RentActAccessoryResponseDto implements Serializable {
    private Long id;
    private String deviceSerialNumber;
    private Long accessoryId;
    private String accessoryName;
    private String modelName;
    private String modelDescription;
    private LocalDate dueDate;
    private LocalDate startDate;
    private Long customerId;
}
