package uz.uzkassa.smartpossupply.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;


@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WaybillWebhookDTO implements Serializable {

    String waybillLocalId;

    Integer currentStateId;

    String currentStateName;

    Integer waybillLocalType;

    String waybillLocalTypeName;

    String waybillNo;

    LocalDate waybillDate;

    String contractNo;

    LocalDate contractDate;

    String consignorTinOrPinfl;

    String consignorName;

    String consignorBranchCode;

    String consignorBranchName;

    String consigneeTinOrPinfl;

    String consigneeName;

    String consigneeBranchCode;

    String consigneeBranchName;

    String responsiblePersonPinfl;

    String responsiblePersonFullName;

    String responsibleCompanyTin;

    String responsibleCompanyName;

    String responsibleCompanyBranchCode;

    String responsibleCompanyBranchName;
}
