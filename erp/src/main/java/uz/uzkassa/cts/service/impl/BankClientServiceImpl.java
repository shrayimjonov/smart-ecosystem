package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.bankclient.request.BankClientDto;
import uz.uzkassa.cts.dto.bankclient.response.BankClientDetailsDto;
import uz.uzkassa.cts.dto.bankclient.response.BankClientUploadDto;
import uz.uzkassa.cts.dto.bankclient.response.BankClientUploadPageableDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BankClientDetailFilter;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.BankClientDetailMapper;
import uz.uzkassa.cts.mappings.BankClientJournalMapper;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.BankClientService;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.LedgerService;
import uz.uzkassa.cts.service.ScpFileService;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BankClientServiceImpl extends BaseServiceImpl<BankClientJournal, Long> implements BankClientService {

    private static final String BANK_SRVC = "BANK_SRVC";
    private static final String BANK_PRCH = "BANK_PRCH";

    private final LedgerService ledgerService;
    private final BankClientJournalRepository repository;
    private final BankClientDetailRepository bankClientDetailRepository;
    private final FileUploadService fileUploadService;
    private final BankClientDetailRejectedRepository rejectedRepository;
    private final ScpFileService scpFileService;
    private final CustomerRepository customerRepository;
    private final BlackListRepository blackListRepository;
    private final BankClientDetailMapper bankClientDetailMapper;
    private final BankClientJournalMapper bankClientJournalMapper;

    public BankClientServiceImpl(LedgerService ledgerService,
                                 BankClientJournalRepository repository,
                                 BankClientDetailRepository bankClientDetailRepository,
                                 FileUploadService fileUploadService,
                                 BankClientDetailRejectedRepository rejectedRepository,
                                 ScpFileService scpFileService,
                                 CustomerRepository customerRepository,
                                 BlackListRepository blackListRepository,
                                 BankClientDetailMapper bankClientDetailMapper,
                                 BankClientJournalMapper bankClientJournalMapper) {
        super(repository);
        this.ledgerService = ledgerService;
        this.repository = repository;
        this.bankClientDetailRepository = bankClientDetailRepository;
        this.fileUploadService = fileUploadService;
        this.rejectedRepository = rejectedRepository;
        this.scpFileService = scpFileService;
        this.customerRepository = customerRepository;
        this.blackListRepository = blackListRepository;
        this.bankClientDetailMapper = bankClientDetailMapper;
        this.bankClientJournalMapper = bankClientJournalMapper;
    }

    @Override
    @Transactional
    public BankClientDto create(BankClientDto dto) {
        BankClientJournal journal = bankClientJournalMapper.toEntity(dto);
        save(journal);
        dto.setId(journal.getId());
        return dto;
    }

    @Override
    public BankClientJournal findTopJournal() {
        return repository.findLastBankClientJournal();
    }

    @Override
    public List<BankClientDetailsDto> createDetails(Long id, MultipartFile file) throws IOException {
        return getBankClientDetailsDtos(id, file);
    }

    private List<BankClientDetailsDto> getBankClientDetailsDtos(Long id, MultipartFile file) throws IOException {
        BankClientJournal journal = findById(id);
        List<BankClientDetail> details = null;
        if (ServiceType.SERVICE.equals(journal.getServiceType())) {
            details = readFileFromXltService(file, journal);
        } else {
            readFileFromXltSell(file, journal);
        }

        return insertIntoBankClientDetails(journal, details);
    }

    @Override
    public List<BankClientDetailsDto> insertIntoBankClientDetails(BankClientJournal journal, List<BankClientDetail> details) {
        List<BankClientDetailsDto> list = new ArrayList<>();
        List<BlackList> blackLists = blackListRepository.findByTinIn(details.stream().map(BankClientDetail::getPayerIdentityNumber).collect(Collectors.toList()));
        Set<String> tinsInBlackList = blackLists.stream().map(BlackList::getTin).collect(Collectors.toSet());
        for (BankClientDetail detail : details) {
            saveToJournal(journal, list, tinsInBlackList, detail);
        }
        return list;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveToJournal(BankClientJournal journal, List<BankClientDetailsDto> list, Set<String> tinsInBlackList, BankClientDetail detail) {
        try {
            if (detail.getId() == null) {

                if (tinsInBlackList.contains(detail.getPayerIdentityNumber())) {
                    BankClientDetailRejected rejected = createRejectedFromDetail(detail,
                        journal.getId(), journal.getName(), BankClientRejectedStatus.IN_BLACK_LIST);
                    rejectedRepository.save(rejected);
                    return;
                }
                Customer customer = customerRepository.findFirstByTinOrOldTin(detail.getPayerIdentityNumber(), detail.getPayerIdentityNumber());
                if (customer == null) {
                    long zero = Long.parseLong(detail.getPayerIdentityNumber());
                    if (zero == 0L)
                        detail.setPayerIdentityNumber("000000000");
                    BankClientDetailRejected rejected = createRejectedFromDetail(detail,
                        journal.getId(), journal.getName(), BankClientRejectedStatus.PENDING);

                    String tin = extractTin(detail.getDescription());
                    Set<String> blackLists = blackListRepository.findAll().stream().map(BlackList::getTin).collect(Collectors.toSet());
                    if (StringUtils.isNotEmpty(tin) && !blackLists.contains(tin)) {
                        Customer customerFromDescription = customerRepository.findFirstByTinOrOldTin(tin, tin);
                        if (customerFromDescription != null) {
                            String oldTin = rejected.getPayerIdentityNumber();
                            rejected.setOldTin(oldTin);
                            rejected.setPayerIdentityNumber(customerFromDescription.getTin());
                        }
                    }
                    rejectedRepository.saveAndFlush(rejected);

                    return;
                }
                if (CustomerStatus.TIN_CHANGED == customer.getStatus()) {
                    BankClientDetailRejected rejected = createRejectedFromDetail(detail,
                        journal.getId(), journal.getName(), BankClientRejectedStatus.IN_BLACK_LIST);
                    rejectedRepository.save(rejected);
                    return;
                }

                bankClientDetailRepository.save(detail);

                BankClientDetailsDto dto = bankClientDetailMapper.toDto(detail);
                dto.setAlreadyExists(false);
                list.add(dto);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            BankClientDetailsDto dto = bankClientDetailMapper.toDto(detail);
            dto.setRejectionDescription(ex.getMessage());
            list.add(dto);
        }
    }

    private String extractTin(String description) {
        List<Pattern> patterns = List.of(
            Pattern.compile("pinfl.*?(\\d{14})", Pattern.CASE_INSENSITIVE),
            Pattern.compile("пинфл.*?(\\d{14})", Pattern.CASE_INSENSITIVE),
            Pattern.compile("jshshir.*?(\\d{14})", Pattern.CASE_INSENSITIVE),
            Pattern.compile("жшшир.*?(\\d{14})", Pattern.CASE_INSENSITIVE),
            Pattern.compile("inn.*?(\\d{9})", Pattern.CASE_INSENSITIVE),
            Pattern.compile("инн.*?(\\d{9})", Pattern.CASE_INSENSITIVE),
            Pattern.compile("stir.*?(\\d{9})", Pattern.CASE_INSENSITIVE)
        );
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(description.replace(" ", "").toLowerCase());
            if (matcher.find()) {
                try {
                    return matcher.group(1);
                } catch (Exception e) {
                    return "";
                }
            }
        }
        return "";
    }

    private BankClientDetailRejected createRejectedFromDetail(BankClientDetail detail, Long journalId,
                                                              String journalName, BankClientRejectedStatus status) {
        BankClientDetailRejected rejected = new BankClientDetailRejected();

        rejected.setDocumentNo(detail.getDocumentNo());
        rejected.setTransactionNumber(detail.getTransactionNumber());
        rejected.setPayerAccountNumber(detail.getPayerAccountNumber());
        rejected.setPayerIdentityNumber(detail.getPayerIdentityNumber());
        rejected.setPayerName(detail.getPayerName());
        rejected.setPayerBankCode(detail.getPayerBankCode());
        rejected.setPayerBankName(detail.getPayerBankName());

        rejected.setReceiverAccountNumber(detail.getReceiverAccountNumber());
        rejected.setReceiverIdentityNumber(detail.getReceiverIdentityNumber());
        rejected.setReceiverName(detail.getReceiverName());
        rejected.setReceiverBankCode(detail.getReceiverBankCode());
        rejected.setReceiverBankName(detail.getReceiverBankName());

        rejected.setAmount(detail.getAmount());
        rejected.setPaymentDate(detail.getPaymentDate());
        rejected.setDescription(detail.getDescription());
        rejected.setContractNumber(detail.getContractNumber());
        rejected.setSpecificationNumber(detail.getSpecificationNumber());
        rejected.setApproved(detail.isApproved());
        rejected.setBankClientJournalId(journalId);
        rejected.setServiceType(detail.getServiceType());
        rejected.setJournalName(journalName);
        rejected.setStatus(status);

        return rejected;
    }

    @Override
    public BankClientUploadDto createDetailWithFile(Long id, MultipartFile file) throws IOException {
        List<BankClientDetailsDto> details = getBankClientDetailsDtos(id, file);
        UploadFileResponse response = createFile(details);
        BankClientJournal journal = findById(id);
        journal.setFileUrl(response.getFileDownloadUri());
        repository.save(journal);

        BankClientUploadDto result = new BankClientUploadDto();
        result.setList(details);
        result.setFileUrl(response.getFileDownloadUri());
        return result;
    }

    @Override
    @Transactional
    public List<BankClientDetailsDto> approveDetails(Long id) {
        Set<BankClientDetail> detailSet = bankClientDetailRepository.findByBankClientJournal_IdAndApproved(id, false);

        ledgerService.importFromJournalDetails(detailSet);

        detailSet.forEach(f -> f.setApproved(true));

        bankClientDetailRepository.saveAll(detailSet);

        List<BankClientDetail> details = new ArrayList<>(detailSet);

        return bankClientDetailMapper.toDto(details);

    }

    @Override
    @Transactional
    public Page<BankClientDto> list(BaseFilter filter) {

        return repository.findAll(filter.getOrderedPageable()).map(bankClientDetailMapper::toDto);
    }

    @Override
    public BankClientDto getById(Long id) {
        return bankClientDetailMapper.toDto(findById(id));
    }

    @Override
    public BankClientUploadDto getDetails(Long id) {
        BankClientJournal journal = findById(id);
        List<BankClientDetail> details = new ArrayList<>(journal.getDetails());
        BankClientUploadDto dto = new BankClientUploadDto();
        dto.setList(bankClientDetailMapper.toDto(details));
        dto.setFileUrl(journal.getFileUrl());

        return dto;
    }

    @Override
    public BankClientUploadPageableDto getDetails(BankClientDetailFilter filter) {

        BankClientJournal journal = findById(filter.getId());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by(Sort.Order.desc("id")));

        Specification<BankClientDetail> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Path<String> documentNo = root.get("documentNo");
            Path<String> payerAccountNumber = root.get("payerAccountNumber");
            Path<String> payerIdentityNumber = root.get("payerIdentityNumber");
            Path<String> payerName = root.get("payerName");
            Path<String> payerBankName = root.get("payerBankName");
            Path<String> description = root.get("description");
            Path<LocalDate> paymentDate = root.get("paymentDate");

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                String search = filter.getSearch().toUpperCase();
                Predicate documentNoPred = criteriaBuilder.like(criteriaBuilder.upper(documentNo), "%" + search + "%");
                Predicate payerAccountNumberPred = criteriaBuilder.like(criteriaBuilder.upper(payerAccountNumber), "%" + search + "%");
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.upper(payerIdentityNumber), "%" + search + "%");
                Predicate tinNamePred = criteriaBuilder.like(criteriaBuilder.upper(payerName), "%" + search + "%");
                Predicate bankNamePred = criteriaBuilder.like(criteriaBuilder.upper(payerBankName), "%" + search + "%");
                Predicate descriptionPred = criteriaBuilder.like(criteriaBuilder.upper(description), "%" + search + "%");
                list.add(criteriaBuilder.or(documentNoPred, payerAccountNumberPred, tinPred, tinNamePred,
                    bankNamePred, descriptionPred));
            }
            if (filter.getPaymentDate() != null)
                list.add(criteriaBuilder.equal(paymentDate, filter.getPaymentDate()));

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };

        Page<BankClientDetailsDto> list;
        if (StringUtils.isNotEmpty(filter.getSearch()) || filter.getPaymentDate() != null) {
            list = bankClientDetailRepository.findAll(specification, pageable).map(bankClientDetailMapper::toDto);
        } else {
            list = bankClientDetailRepository.findByBankClientJournal_IdOrderByIdDesc(filter.getId(), pageable)
                .map(bankClientDetailMapper::toDto);
        }

        BankClientUploadPageableDto dto = new BankClientUploadPageableDto();
        dto.setList(list);
        dto.setFileUrl(journal.getFileUrl());

        return dto;
    }

    @Override
    public void uploadTest(MultipartFile file) {
        try {
            scpFileService.upload(file.getOriginalFilename(), file.getBytes());
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private UploadFileResponse createFile(List<BankClientDetailsDto> details) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Statuses");

        createHeader(sheet);

        int rowCount = 1;
        for (BankClientDetailsDto detail : details) {
            Row row = sheet.createRow(++rowCount);
            createRow(detail, row);
        }

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] bytes = bos.toByteArray();
            return fileUploadService.upload(bytes, "upload.xls", "application/vnd.ms-excel",
                (long) bos.size(), 0L,
                EntityTypeEnum.BANK_CLIENT.name(), DocumentType.SERVICE_SPECIFICATION.name());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private void createRow(BankClientDetailsDto detail, Row row) {
        row.createCell(0).setCellValue(detail.getDocumentNo());
        row.createCell(1).setCellValue(detail.getPayerAccountNumber());
        row.createCell(2).setCellValue(detail.getPayerIdentityNumber());
        row.createCell(3).setCellValue(detail.getPayerName());
        row.createCell(4).setCellValue(detail.getPayerBankCode());
        row.createCell(5).setCellValue(detail.getPayerBankName());
        row.createCell(6).setCellValue(detail.getReceiverAccountNumber());
        row.createCell(7).setCellValue(detail.getReceiverIdentityNumber());
        row.createCell(8).setCellValue(detail.getReceiverName());
        row.createCell(9).setCellValue(detail.getReceiverBankCode());
        row.createCell(10).setCellValue(detail.getReceiverBankName());
        row.createCell(11).setCellValue(detail.getAmount().toString());
        row.createCell(12).setCellValue(detail.getPaymentDate());
        row.createCell(13).setCellValue(detail.getDescription());
        row.createCell(14).setCellValue(detail.isApproved());
        row.createCell(15).setCellValue(detail.isAlreadyExists());
        row.createCell(16).setCellValue(detail.getRejectionDescription());
    }

    private void createHeader(XSSFSheet sheet) {
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("documentNo");
        header.createCell(1).setCellValue("payerAccountNumber");
        header.createCell(2).setCellValue("payerIdentityNumber");
        header.createCell(3).setCellValue("payerName");
        header.createCell(4).setCellValue("payerBankCode");
        header.createCell(5).setCellValue("payerBankName");
        header.createCell(6).setCellValue("receiverAccountNumber");
        header.createCell(7).setCellValue("receiverIdentityNumber");
        header.createCell(8).setCellValue("receiverName");
        header.createCell(9).setCellValue("receiverBankCode");
        header.createCell(10).setCellValue("receiverBankName");
        header.createCell(11).setCellValue("amount");
        header.createCell(12).setCellValue("paymentDate");
        header.createCell(13).setCellValue("description");
        header.createCell(14).setCellValue("approved");
        header.createCell(15).setCellValue("alreadyExists");
        header.createCell(16).setCellValue("rejectionDescription");
    }

    private List<BankClientDetail> readFileFromXltService(MultipartFile file, BankClientJournal journal) throws IOException {
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(file.getInputStream());

        // Getting the Sheet at index zero
        Sheet sheet = workbook.getSheetAt(0);

        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();

        List<BankClientDetail> bankClientDetails = new ArrayList<>();

        // 2. Or you can use a for-each loop to iterate over the rows and columns
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        for (Row row : sheet) {

            String tin = dataFormatter.formatCellValue(row.getCell(10));
            if (StringUtils.isEmpty(tin))
                continue;

            BankClientDetail detail = new BankClientDetail();

            try {
                detail.setPaymentDate(LocalDate.parse(row.getCell(0).toString(), formatter));
            } catch (Exception ex) {
                continue;
            }
            detail.setPayerAccountNumber(dataFormatter.formatCellValue(row.getCell(1)));
            detail.setPayerName(dataFormatter.formatCellValue(row.getCell(2)));
            detail.setPayerBankCode(dataFormatter.formatCellValue(row.getCell(5)));
            detail.setPayerBankName("");
            detail.setPayerIdentityNumber(dataFormatter.formatCellValue(row.getCell(10)));

            detail.setDocumentNo(dataFormatter.formatCellValue(row.getCell(3)));
            String amount = dataFormatter.formatCellValue(row.getCell(7)).replaceAll(",", "");
            detail.setAmount(new BigDecimal(amount));
            detail.setDescription(dataFormatter.formatCellValue(row.getCell(8)));
            detail.setServiceType(ServiceType.SERVICE);

            detail.setReceiverIdentityNumber(BANK_SRVC);
            detail.setReceiverAccountNumber("20208000905065950001");
            detail.setReceiverName("ООО \"SPECIAL TEXNO SOFT\"");
            detail.setReceiverBankCode("01088");
            detail.setReceiverBankName("Головной Офис");

            detail.setBankClientJournal(journal);
            bankClientDetails.add(detail);


//            journal.addBankClientDetail(detail);

        }
        return bankClientDetails;
    }


    private void readFileFromXltSell(MultipartFile file, BankClientJournal journal) throws IOException {
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(file.getInputStream());

        // Getting the Sheet at index zero
        Sheet sheet = workbook.getSheetAt(0);

        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        // 2. Or you can use a for-each loop to iterate over the rows and columns
        for (Row row : sheet) {
            if (row.getCell(0).toString().matches("\\d+")) {
                BankClientDetail detail = new BankClientDetail();

                detail.setDocumentNo(dataFormatter.formatCellValue(row.getCell(0)));

                detail.setReceiverIdentityNumber(BANK_PRCH);
                detail.setServiceType(ServiceType.SALE);

                detail.setPayerAccountNumber(dataFormatter.formatCellValue(row.getCell(1)));
                detail.setPayerIdentityNumber(dataFormatter.formatCellValue(row.getCell(2)));
                detail.setPayerName(dataFormatter.formatCellValue(row.getCell(3)));
                detail.setPayerBankCode(dataFormatter.formatCellValue(row.getCell(4)));
                detail.setPayerBankName(dataFormatter.formatCellValue(row.getCell(5)));

                detail.setReceiverAccountNumber(dataFormatter.formatCellValue(row.getCell(6)));
                detail.setReceiverIdentityNumber(dataFormatter.formatCellValue(row.getCell(7)));
                detail.setReceiverName(dataFormatter.formatCellValue(row.getCell(8)));
                detail.setReceiverBankCode(dataFormatter.formatCellValue(row.getCell(9)));
                detail.setReceiverBankName(dataFormatter.formatCellValue(row.getCell(10)));

                try {
                    String amount = dataFormatter.formatCellValue(row.getCell(11)).replaceAll(",", ".");
                    detail.setAmount(new BigDecimal(amount));
                } catch (NumberFormatException ex) {
                    throw validationService.badRequestExceptionThrow(AMOUNT_FORMAT_ERROR).get();
                }

                detail.setPaymentDate(LocalDate.parse(row.getCell(12).toString(), formatter));
                detail.setDescription(dataFormatter.formatCellValue(row.getCell(13)));

                journal.addBankClientDetail(detail);
            }
        }

        workbook.close();
    }
}
