package uz.uzkassa.horeca.dto.billing.customerpublicoffer;

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
