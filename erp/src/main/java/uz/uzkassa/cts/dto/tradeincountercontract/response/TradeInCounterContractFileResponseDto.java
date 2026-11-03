package uz.uzkassa.cts.dto.tradeincountercontract.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class TradeInCounterContractFileResponseDto implements Serializable {
    private Long contractId;
    private String fileUri;
    private String fileName;
    private String contentType;
}
