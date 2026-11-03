package uz.uzkassa.cts.dto.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class OfdApplicationSendPayload implements Serializable {

    private Long fiscalizationId;
    private String apiUrl;
    private String contractOfferBase64;
    private String contractOfferName;

}
