package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.BankClientDetail;
import uz.uzkassa.cts.domain.BankClientDetailRejected;
import uz.uzkassa.cts.domain.BankClientJournal;
import uz.uzkassa.cts.enums.BankClientRejectedStatus;
import uz.uzkassa.cts.repository.BankClientDetailRejectedRepository;
import uz.uzkassa.cts.repository.BankClientDetailRepository;
import uz.uzkassa.cts.service.ImportToLedgerService;
import uz.uzkassa.cts.service.LedgerService;

@Component
@Slf4j
public class ImportToLedgerServiceImpl implements ImportToLedgerService {

    private final BankClientDetailRejectedRepository repository;
    private final LedgerService ledgerService;
    private final BankClientDetailRepository bankClientDetailRepository;


    public ImportToLedgerServiceImpl(BankClientDetailRejectedRepository repository, LedgerService ledgerService, BankClientDetailRepository bankClientDetailRepository) {
        this.repository = repository;
        this.ledgerService = ledgerService;
        this.bankClientDetailRepository = bankClientDetailRepository;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void importToLedger(BankClientDetailRejected rejected, BankClientJournal journal) {
        BankClientDetail detail = new BankClientDetail();
        detail.setBankClientJournal(journal);

        detail.setDocumentNo(rejected.getDocumentNo());
        detail.setTransactionNumber(rejected.getTransactionNumber());
        detail.setPayerAccountNumber(rejected.getPayerAccountNumber());
        detail.setPayerIdentityNumber(rejected.getPayerIdentityNumber());
        detail.setPayerName(rejected.getPayerName());
        detail.setPayerBankCode(rejected.getPayerBankCode());
        detail.setPayerBankName(rejected.getPayerBankName());

        detail.setReceiverAccountNumber(rejected.getReceiverAccountNumber());
        detail.setReceiverIdentityNumber(rejected.getReceiverIdentityNumber());
        detail.setReceiverName(rejected.getReceiverName());
        detail.setReceiverBankCode(rejected.getReceiverBankCode());
        detail.setReceiverBankName(rejected.getReceiverBankName());

        detail.setAmount(rejected.getAmount());
        detail.setPaymentDate(rejected.getPaymentDate());
        detail.setDescription(rejected.getDescription());
        detail.setContractNumber(rejected.getContractNumber());
        detail.setSpecificationNumber(rejected.getSpecificationNumber());
        detail.setApproved(rejected.isApproved());
        detail.setServiceType(rejected.getServiceType());

        ledgerService.importFromJournalDetail(detail);

        detail.setApproved(true);
        bankClientDetailRepository.save(detail);

        rejected.setStatus(BankClientRejectedStatus.ACCEPTED);
        repository.save(rejected);
    }
}
