package uz.uzkassa.silen.dto.callback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaybillCallback implements Serializable {
    private String waybillLocalId;
    private Integer currentStateId;
    private String currentStateName;
    private String waybillNo;
    private LocalDate waybillDate;
    private String contractNo;
    private LocalDate contractDate;
    private String consignorTinOrPinfl;
    private String consignorName;
    private String consignorBranchCode;
    private String consignorBranchName;
    private String consigneeTinOrPinfl;
    private String consigneeName;
    private String consigneeBranchCode;
    private String consigneeBranchName;
    private String responsiblePersonPinfl;
    private String responsiblePersonFullName;
}
