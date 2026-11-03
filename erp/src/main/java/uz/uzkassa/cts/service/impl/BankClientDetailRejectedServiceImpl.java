package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.BankClientDetailRejected;
import uz.uzkassa.cts.domain.BankClientDetailRejected_;
import uz.uzkassa.cts.domain.BankClientJournal;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.bankclientdetailrejected.request.BankClientDetailsRejectEditRequestDto;
import uz.uzkassa.cts.dto.bankclientdetailrejected.response.BankClientDetailRejectedResponseDto;
import uz.uzkassa.cts.enums.BankClientRejectedStatus;
import uz.uzkassa.cts.filter.BankClientDetailRejectedFilter;
import uz.uzkassa.cts.mappings.BankClientDetailRejectedMapper;
import uz.uzkassa.cts.repository.BankClientDetailRejectedRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.service.BankClientDetailRejectedService;
import uz.uzkassa.cts.service.BankClientService;
import uz.uzkassa.cts.service.ImportToLedgerService;
import uz.uzkassa.cts.utils.InvoiceCreateExcelUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
public class BankClientDetailRejectedServiceImpl extends BaseServiceImpl<BankClientDetailRejected, Long> implements BankClientDetailRejectedService {

    private final BankClientDetailRejectedRepository repository;
    private final BankClientService bankClientService;
    private final ImportToLedgerService importToLedgerService;
    private final CustomerRepository customerRepository;
    private final BankClientDetailRejectedMapper bankClientDetailRejectedMapper;

    public BankClientDetailRejectedServiceImpl(BankClientDetailRejectedRepository repository,
                                               BankClientService bankClientService,
                                               ImportToLedgerService importToLedgerService,
                                               CustomerRepository customerRepository,
                                               BankClientDetailRejectedMapper bankClientDetailRejectedMapper) {
        super(repository);
        this.repository = repository;
        this.bankClientService = bankClientService;
        this.importToLedgerService = importToLedgerService;
        this.customerRepository = customerRepository;
        this.bankClientDetailRejectedMapper = bankClientDetailRejectedMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BankClientDetailRejectedResponseDto> list(BankClientDetailRejectedFilter filter) {
        Page<BankClientDetailRejected> details = repository.findAll(getSpecification(filter), filter.getOrderedPageable());
        return details.map(bankClientDetailRejectedMapper::toDto);
    }

    @Override
    public void resend(List<Long> ids) {
        List<BankClientDetailRejected> rejecteds = repository.findByStatusAndIdInOrderByBankClientJournalId(BankClientRejectedStatus.PENDING, ids);
        Long journalId = 0L;
        BankClientJournal journal = null;

        int exceptionsCount = 0;
        for (BankClientDetailRejected rejected : rejecteds) {
            if (!journalId.equals(rejected.getBankClientJournalId())) {
                journal = bankClientService.findById(rejected.getBankClientJournalId());
                journalId = journal.getId();
            }
            try {
                Customer customer = customerRepository.findFirstByTinOrOldTin(rejected.getPayerIdentityNumber(), rejected.getPayerIdentityNumber());
                if (customer == null) {
                    continue;
                }

                importToLedgerService.importToLedger(rejected, journal);
            } catch (DataIntegrityViolationException e) {

                rejected.setStatus(BankClientRejectedStatus.HAS_BEEN_ADDED);
                repository.save(rejected);

            } catch (Exception ex) {
                exceptionsCount++;
            }
        }
        if (exceptionsCount > 0)
            throw validationService.conflictExceptionThrow(CONFLICT_ROWS_IN_BANK).get();
    }

    @Override
    public void changeStatus(Long id) {
        BankClientDetailRejected entity = findById(id);
        if (BankClientRejectedStatus.ACCEPTED.equals(entity.getStatus()))
            throw validationService.statusChangeExceptionThrow(BankClientRejectedStatus.ACCEPTED.getName()).get();
        entity.setStatus(BankClientRejectedStatus.EXPIRED);
        save(entity);
    }

    @Override
    public void deleteIfStatusPending(Long id) {
        BankClientDetailRejected rejected = repository.findByStatusAndId(BankClientRejectedStatus.PENDING, id);
        if (rejected != null)
            delete(rejected);
    }

    @Override
    public void updateInn(BankClientDetailsRejectEditRequestDto dto) {
        BankClientDetailRejected rejected = repository.findByStatusInAndId(
                List.of(BankClientRejectedStatus.PENDING, BankClientRejectedStatus.IN_BLACK_LIST),
                dto.getId());

        if (rejected != null) {
            if (StringUtils.isEmpty(rejected.getOldTin())) {
                String oldTin = rejected.getPayerIdentityNumber();
                rejected.setOldTin(oldTin);
            }
            rejected.setPayerIdentityNumber(dto.getTin());

            if (BankClientRejectedStatus.IN_BLACK_LIST.equals(rejected.getStatus()))
                rejected.setStatus(BankClientRejectedStatus.PENDING);

            repository.save(rejected);
        }
    }


    public byte[] download(BankClientDetailRejectedFilter filter) {
        List<BankClientDetailRejected> details = repository.findAll(getSpecification(filter));
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("TemporaryRejected");
            String[] headerNames = new String[]{"ИНН", "Название компании", "Сумма",
                    "Название журнала", "Описание", "Дата оплаты", "Дата создания в системе", "Дата последнего изминения в системе", "Статус", "Старый ИНН"};
            InvoiceCreateExcelUtil.createHeader(headerNames, sheet);
            InvoiceCreateExcelUtil.createRejectedListReport(details, sheet);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new byte[0];
    }

    private Specification<BankClientDetailRejected> getSpecification(BankClientDetailRejectedFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> documentNo = root.get(BankClientDetailRejected_.DOCUMENT_NO);
                Path<String> payerIdentityNumber = root.get(BankClientDetailRejected_.PAYER_IDENTITY_NUMBER);
                Path<String> payerName = root.get(BankClientDetailRejected_.PAYER_NAME);
                Path<String> journalName = root.get(BankClientDetailRejected_.JOURNAL_NAME);
                Path<String> description = root.get(BankClientDetailRejected_.DESCRIPTION);
                Path<String> oldTin = root.get(BankClientDetailRejected_.OLD_TIN);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(documentNo), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(payerIdentityNumber, filter.getSearchForQuery());
                Predicate operatorPred = criteriaBuilder.like(criteriaBuilder.lower(payerName), filter.getSearchForQuery());
                Predicate journalNamePred = criteriaBuilder.like(journalName, filter.getSearchForQuery());
                Predicate descriptionPred = criteriaBuilder.like(criteriaBuilder.lower(description), filter.getSearchForQuery());
                Predicate oldTinPred = criteriaBuilder.like(criteriaBuilder.lower(oldTin), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(customerNamePred, tinPred, operatorPred, journalNamePred, descriptionPred, oldTinPred));
            }

            if (filter.getStatus() != null) {
                Path<BankClientRejectedStatus> statusPath = root.get(BankClientDetailRejected_.STATUS);
                list.add(criteriaBuilder.equal(statusPath, BankClientRejectedStatus.valueOf(filter.getStatus())));
            }

            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDate> paymentDate = root.get(BankClientDetailRejected_.PAYMENT_DATE);
                list.add(criteriaBuilder.between(paymentDate, filter.getFrom(), filter.getTo()));
            }
            if (filter.getOnlyChanged() != null) {
                Path<String> oldTin = root.get(BankClientDetailRejected_.OLD_TIN);
                list.add(!filter.getOnlyChanged() ? criteriaBuilder.isNull(oldTin) : criteriaBuilder.isNotNull(oldTin));
            }

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }

}


