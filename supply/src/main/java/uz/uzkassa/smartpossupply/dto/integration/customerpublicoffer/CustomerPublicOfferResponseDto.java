package uz.uzkassa.smartpossupply.dto.integration.customerpublicoffer;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class CustomerPublicOfferResponseDto implements Serializable {

    private String id;
    private String publicOfferId;
    private String publicOfferContent;
    private String customerId;
    private String customerTin;
    private LocalDateTime createdDate;
}
