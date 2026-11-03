package uz.uzkassa.cts.dto.orderspecification.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.cts.enums.ContractStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrderSpecificationResponseDto implements Serializable {

    private Long id;
    private String specFileUri;
    private String specFileName;
    private String specContentType;
    @Enumerated(EnumType.STRING)
    private ContractStatus status;
    private Long orderId;
    private Long contractId;
    private LocalDateTime createdDate;
    private String number;

    private List<OrderSpecificationDetailsResponseDto> orderSpecificationDetails = new ArrayList<>();

}
