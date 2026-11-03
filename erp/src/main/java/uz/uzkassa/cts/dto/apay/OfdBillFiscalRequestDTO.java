package uz.uzkassa.cts.dto.apay;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ServiceReferenceType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class OfdBillFiscalRequestDTO implements Serializable {

    String billId;
    OfdCommitmentDTO merchantInfo;
    ServiceReferenceType referenceType;
    List<OfdBillItem> items;

    Long totalVat;
    Long receivedECash;
    String time;
}
