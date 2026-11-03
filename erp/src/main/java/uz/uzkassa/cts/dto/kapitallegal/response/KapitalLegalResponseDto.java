package uz.uzkassa.cts.dto.kapitallegal.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDirectionType;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentState;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class KapitalLegalResponseDto implements Serializable {

    private Long id;
    //    private String accountCredit;
    private String accountDebit;
    private BigDecimal amount;
    private String branch;
    private String documentDate;
    private KapitalLegalDirectionType directionType;
    private KapitalLegalDocumentType documentType;
    //    private String absError;
    private String absErrorMsgDescription;
    private String absId;
    //    private String tinCredit;
    private String tinDebit;
    //    private String mfoCredit;
    private String mfoDebit;
    //    private String nameCredit;
    private String nameDebit;
    private String documentNumber;
    //    private String purpCode;
    private String purpose;
    private KapitalLegalDocumentState state;
    //    private String uniq;
    private String time;
    private boolean sentToBilling;
}
