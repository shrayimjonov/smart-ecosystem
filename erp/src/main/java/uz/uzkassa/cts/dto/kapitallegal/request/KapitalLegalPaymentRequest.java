package uz.uzkassa.cts.dto.kapitallegal.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class KapitalLegalPaymentRequest implements Serializable {
    private KapitalLegalDocumentRequest document;
    private KapitalLegalSignRequest signs;
}
