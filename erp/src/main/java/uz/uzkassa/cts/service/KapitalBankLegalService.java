package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.kapitallegal.request.KapitalLegalGetDocInfoRequest;
import uz.uzkassa.cts.dto.kapitallegal.request.KapitalLegalGetDocsRequest;
import uz.uzkassa.cts.dto.kapitallegal.request.KapitalLegalPaymentRequest;
import uz.uzkassa.cts.dto.kapitallegal.response.*;

public interface KapitalBankLegalService {

    KapitalLegalLoginResponse authenticate();

    KapitalLegalDoc1CResponse get1CDocuments(KapitalLegalGetDocsRequest request);

    KapitalLegalGetDocInfoResponse getAccountInfo(KapitalLegalGetDocInfoRequest request);

    KapitalLegalSendPaymentResponse sendPayment(KapitalLegalPaymentRequest request);

    KapitalLegalCheckPaymentResponse checkPayment(KapitalLegalPaymentRequest request);
}
