package uz.uzkassa.smartpossupply.dto.integration.publicoffer;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PublicOfferResponseDto implements Serializable {

    private String id;
    private String content;
    private Boolean active;
}
