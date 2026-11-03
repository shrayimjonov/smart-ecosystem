package uz.uzkassa.smartpossupply.dto.factura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvestmentObjectDTO implements Serializable {
    private String objectId;
    private String objectName;
    private List<LotDTO> lots;
    private List<String> accounts;

    @Getter
    @Setter
    private static class LotDTO implements Serializable {
        private String lotPrefix;
        private String lotNumber;
    }
}
