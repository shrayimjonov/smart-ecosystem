package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.KapitalLegalPayment;
import uz.uzkassa.cts.domain.KapitalLegalPaymentDetail;
import uz.uzkassa.cts.dto.kapitallegal.request.KapitalLegalGetDocsRequest;
import uz.uzkassa.cts.dto.kapitallegal.response.KapitalLegal1CResult;
import uz.uzkassa.cts.dto.kapitallegal.response.KapitalLegalDoc1CResponse;
import uz.uzkassa.cts.dto.kapitallegal.response.KapitalLegel1CContent;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentState;
import uz.uzkassa.cts.repository.KapitalLegalPaymentRepository;
import uz.uzkassa.cts.service.KapitalBankLegalService;
import uz.uzkassa.cts.service.KapitalLegalPaymentService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class KapitalLegalPaymentServiceImpl extends BaseServiceImpl<KapitalLegalPayment, Long> implements KapitalLegalPaymentService {

    private final KapitalBankLegalService kapitalBankLegalService;
    private final KapitalLegalPaymentRepository repository;

    public KapitalLegalPaymentServiceImpl(KapitalLegalPaymentRepository repository,
                                          KapitalBankLegalService kapitalBankLegalService) {
        super(repository);
        this.repository = repository;
        this.kapitalBankLegalService = kapitalBankLegalService;
    }


    @Override
    public void checkForPayments() {
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        syncByDate(formattedDate);
        KapitalLegalPayment payment = repository.findTopOrderByIdDesc();
        if (!formattedDate.equals(payment.getOperDay()))
            syncByDate(payment.getOperDay());
    }

    public void syncByDate(String date) {
        KapitalLegalGetDocsRequest request = createRequest(date);
        KapitalLegalDoc1CResponse response = kapitalBankLegalService.get1CDocuments(request);

        if (response.getError() != null) {
            throw validationService.badRequestExceptionThrow("Error from KapitalBank STATUS: " + response.getError().getCode() + " Message: " + response.getError().getMessage()).get();
        }

        KapitalLegalPayment kapitalLegalPayment = new KapitalLegalPayment();
        Optional<KapitalLegalPayment> payment = repository.findByOperDay(date);
        if (payment.isPresent())
            kapitalLegalPayment = payment.get();
        else {
            kapitalLegalPayment.setOperDay(request.getDate());
        }

        kapitalLegalPayment.setResponseId(response.getId());
        KapitalLegal1CResult result = response.getResult();
        kapitalLegalPayment.setDayStatus(result.getDayStatus());
        kapitalLegalPayment.setSaldoIn(result.getSaldoIn());
        kapitalLegalPayment.setSaldoOut(result.getSaldoOut());
        kapitalLegalPayment.setTotalCredit(result.getTotalCredit());
        kapitalLegalPayment.setTotalDebit(result.getTotalDebit());

        Set<KapitalLegalPaymentDetail> detailSet = kapitalLegalPayment.getKapitalLegalPaymentDetails();
        List<KapitalLegel1CContent> contents = result.getContent();
        for (KapitalLegel1CContent content : contents) {
            if (!excludedStates().contains(content.getState())) {
                KapitalLegalPaymentDetail detail = mapToDetail(content);
                detail.setKapitalLegalPayment(kapitalLegalPayment);
                detailSet.add(detail);
            }
        }
        repository.save(kapitalLegalPayment);
    }

    private List<KapitalLegalDocumentState> excludedStates(){
        return Arrays.asList(KapitalLegalDocumentState.INTRODUCED, KapitalLegalDocumentState.DELAYED);
    }

    private KapitalLegalPaymentDetail mapToDetail(KapitalLegel1CContent content) {
        KapitalLegalPaymentDetail detail = new KapitalLegalPaymentDetail();
        detail.setAccountCredit(content.getAccountCredit());
        detail.setAccountDebit(content.getAccountDebit());
        detail.setAmount(content.getAmount());
        detail.setBranch(content.getBranch());
        detail.setDocumentDate(content.getDocumentDate());
        detail.setDirectionType(content.getDirectionType());
        detail.setDocumentType(content.getDocumentType());
        detail.setAbsError(content.getAbsError());
        detail.setAbsErrorMsgDescription(content.getAbsErrorMsgDescription());
        detail.setAbsId(content.getAbsId());
        detail.setTinCredit(StringUtils.isNotEmpty(content.getTinCredit()) ? content.getTinCredit().replaceAll("\\s+", "") : null);
        detail.setTinDebit(StringUtils.isNotEmpty(content.getTinDebit()) ? content.getTinDebit().replaceAll("\\s+", "") : null);
        detail.setMfoCredit(content.getMfoCredit());
        detail.setMfoDebit(content.getMfoDebit());
        detail.setNameCredit(content.getNameCredit());
        detail.setNameDebit(content.getNameDebit());
        detail.setDocumentNumber(content.getDocumentNumber());
        detail.setPurpCode(content.getPurpCode());
        String desc = content.getPurpCode() != null ? content.getPurpCode() + content.getPurpose() : content.getPurpose();
        detail.setPurpose(desc);
        detail.setState(content.getState());
        detail.setUniq(content.getUniq());
        detail.setTime(content.getTime());
        return detail;
    }


    private KapitalLegalGetDocsRequest createRequest(String formattedDate) {
        KapitalLegalGetDocsRequest todayRequest = new KapitalLegalGetDocsRequest();
        todayRequest.setDate(formattedDate);
        todayRequest.setBranch("01088");
        todayRequest.setAccount("20208000905065950001");
//        todayRequest.setBranch("00954");
//        todayRequest.setAccount("23510000205065950250");
        return todayRequest;
    }
}
