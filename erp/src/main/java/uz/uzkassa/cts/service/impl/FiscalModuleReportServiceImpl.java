package uz.uzkassa.cts.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.FiscalModuleReport;
import uz.uzkassa.cts.domain.cases.Cases;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.fm.FMReportUnCheckedExcelResponseDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleReportListDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleReportRequestDTO;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.enums.ArrivalStatus;
import uz.uzkassa.cts.enums.CaseType;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.enums.SoliqTransferStatus;
import uz.uzkassa.cts.filter.FiscalModuleReportFilter;
import uz.uzkassa.cts.helper.excelhelper.XlsxWriter;
import uz.uzkassa.cts.repository.CaseRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.repository.FiscalModuleReportRepository;
import uz.uzkassa.cts.service.FiscalModuleReportService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class FiscalModuleReportServiceImpl extends BaseServiceImpl<FiscalModuleReport, Long> implements FiscalModuleReportService {

    private final FiscalModuleReportRepository fiscalModuleReportRepository;
    private final CustomerRepository customerRepository;
    private final ObjectMapper objectMapper;
    private final XlsxWriter xlsxWriter;
    private final CaseRepository caseRepository;
    private final ValidationContext validationContext;

    public FiscalModuleReportServiceImpl(FiscalModuleReportRepository fiscalModuleReportRepository,
                                         CustomerRepository customerRepository,
                                         ObjectMapper objectMapper,
                                         XlsxWriter xlsxWriter, CaseRepository caseRepository,
                                         ValidationContext validationContext) {
        super(fiscalModuleReportRepository);
        this.fiscalModuleReportRepository = fiscalModuleReportRepository;
        this.customerRepository = customerRepository;
        this.objectMapper = objectMapper;
        this.xlsxWriter = xlsxWriter;
        this.caseRepository = caseRepository;
        this.validationContext = validationContext;
    }

    @Override
    public Page<FiscalModuleReportListDTO> getList(FiscalModuleReportFilter filter) {
        ResultList<FiscalModuleReport> resultList = fiscalModuleReportRepository.getResultList(filter);
        List<FiscalModuleReportListDTO> result = resultList
                .getList()
                .stream()
                .map(fiscalModuleReport -> {
                    FiscalModuleReportListDTO fiscalModuleReportListDTO = new FiscalModuleReportListDTO();
                    fiscalModuleReportListDTO.setId(fiscalModuleReport.getId());
                    fiscalModuleReportListDTO.setCustomer(fiscalModuleReport.getCustomer().toCustomerShortDTO());
                    fiscalModuleReportListDTO.setDeviceSerialNumber(fiscalModuleReport.getDeviceSerialNumber());
                    fiscalModuleReportListDTO.setFmSerialNumber(fiscalModuleReport.getFmSerialNumber());
                    fiscalModuleReportListDTO.setSoliqTransferStatus(fiscalModuleReport.getSoliqTransferStatus());

                    if (fiscalModuleReport.getCases() != null) {
                        fiscalModuleReportListDTO.setCaseId(fiscalModuleReport.getCaseId());
                        if (fiscalModuleReport.getCases().getCaseType() != null) {
                            String statusReason = fiscalModuleReport.getCases().getCaseType().getName();
                            if (fiscalModuleReport.getReason() != null && FMReportReason.NO_REASON != fiscalModuleReport.getReason()) {
                                statusReason = statusReason.concat("(").concat(fiscalModuleReport.getReason().getName()).concat(")");
                            }
                            fiscalModuleReportListDTO.setStatusReason(statusReason);
                        }
                        fiscalModuleReportListDTO.setOperator(fiscalModuleReport.getCases().getCreator().toCommonDTO());
                        fiscalModuleReportListDTO.setCaseType(fiscalModuleReport.getCases().getCaseType());
                    }

                    fiscalModuleReportListDTO.setArrivalStatus(fiscalModuleReport.getArrivalStatus());
                    fiscalModuleReportListDTO.setArrivalDate(fiscalModuleReport.getArrivalDate());

                    if (fiscalModuleReport.getCustomer() != null && fiscalModuleReport.getCustomer().getAddress().getRegionId() != null) {
                        fiscalModuleReportListDTO.setRegion(fiscalModuleReport.getCustomer().getAddress().getRegion().toCommonDTO());
                    }
                    fiscalModuleReportListDTO.setCreatedDate(fiscalModuleReport.getCreatedDate());
                    if (fiscalModuleReport.getLastModifierId() != null) {
                        fiscalModuleReportListDTO.setLastModifier(fiscalModuleReport.getLastModifier().getName());
                    }
                    fiscalModuleReportListDTO.setReason(fiscalModuleReport.getReason());
                    return fiscalModuleReportListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public Long create(FiscalModuleReportRequestDTO fiscalModuleReportRequestDTO) {
        validationContext.validate(fiscalModuleReportRequestDTO);
        FiscalModuleReport fiscalModuleReport = new FiscalModuleReport();
        fiscalModuleReport.setCustomerId(fiscalModuleReportRequestDTO.getCustomerId());
        fiscalModuleReport.setDeviceSerialNumber(fiscalModuleReportRequestDTO.getDeviceSerialNumber());
        fiscalModuleReport.setFmSerialNumber(fiscalModuleReportRequestDTO.getFmSerialNumber());
        fiscalModuleReport.setArrivalStatus(fiscalModuleReportRequestDTO.getStatus());
        fiscalModuleReport.setReason(fiscalModuleReportRequestDTO.getReason());
        fiscalModuleReport.setSoliqTransferStatus(fiscalModuleReportRequestDTO.getSoliqTransferStatus());
        return fiscalModuleReportRepository.save(fiscalModuleReport).getId();
    }

    @Override
    @Transactional
    public Long updateStatus(Long id, ArrivalStatus arrivalStatus, FMReportReason reason) {
        FiscalModuleReport fiscalmodulereport = findById(id);
        if (arrivalStatus != null) {
            fiscalmodulereport.setArrivalStatus(arrivalStatus);
            fiscalmodulereport.setArrivalDate(LocalDateTime.now());
        } else {
            fiscalmodulereport.setReason(reason);
        }
        return fiscalModuleReportRepository.save(fiscalmodulereport).getId();
    }

    @Override
    @Transactional
    public void create(Payload payload) {
        Cases cases = caseRepository.findById(payload.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CASE));
        FiscalModuleReport fiscalModuleReport = new FiscalModuleReport();
        fiscalModuleReport.setCaseId(cases.getId());
        fiscalModuleReport.setCustomerId(cases.getCustomerId());
        fiscalModuleReport.setArrivalStatus(ArrivalStatus.NOT_COME);
        if (CaseType.DEREGISTER_KKM == cases.getCaseType()) {
            if (getClassFieldValue(cases)) {
                fiscalModuleReport.setReason(FMReportReason.FM_LOST);
            } else {
                fiscalModuleReport.setReason(FMReportReason.NO_REASON);
            }
            fiscalModuleReport.setDeviceSerialNumber(getClassFieldValue(cases, "deviceSerialNumber"));
            fiscalModuleReport.setFmSerialNumber(getClassFieldValue(cases, "fmSerialNumber"));
        } else if (CaseType.REPLACEMENT_FM == cases.getCaseType()) {
            String reason = getClassFieldValue(cases, "reason");
            if (StringUtils.isNotEmpty(reason)) {
                fiscalModuleReport.setReason(FMReportReason.valueOf(reason));
            }
            fiscalModuleReport.setDeviceSerialNumber(getClassFieldValue(cases, "deviceSerialNumber"));
            fiscalModuleReport.setFmSerialNumber(getClassFieldValue(cases, "fiscalModuleSerial"));
        }
        if (fiscalModuleReportRepository.existsByFmSerialNumber(fiscalModuleReport.getFmSerialNumber())) {
            throw validationService.entityAlreadyExistExceptionThrow(FISCAL_MODULE).get();
        }
        fiscalModuleReportRepository.save(fiscalModuleReport);
    }

    @Override
    @Transactional
    public byte[] importFiscalModulesFromExcel(MultipartFile file) {
        Set<FMReportUnCheckedExcelResponseDTO> uncheckedSerialNumbers = new HashSet<>();
        List<FiscalModuleReport> fiscalModuleReports = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row cells : sheet) {
                if (cells.getCell(0) != null) {
                    String serialNumber = String.valueOf(cells.getCell(0)).toUpperCase();
                    fiscalModuleReportRepository.findFirstByFmSerialNumber(serialNumber.trim()).ifPresentOrElse(fiscalModuleReport -> {
                        fiscalModuleReport.setArrivalDate(LocalDateTime.now());
                        fiscalModuleReport.setArrivalStatus(ArrivalStatus.CAME);
                        fiscalModuleReports.add(fiscalModuleReport);
                    }, () -> uncheckedSerialNumbers.add(new FMReportUnCheckedExcelResponseDTO(serialNumber)));
                }
            }
        } catch (Exception e) {
            log.error("Import fm serialNumbers report xls file failed", e);
            return new byte[0];
        }
        if (!CollectionUtils.isEmpty(fiscalModuleReports)) {
            fiscalModuleReportRepository.saveAll(fiscalModuleReports);
        }
        if (!CollectionUtils.isEmpty(uncheckedSerialNumbers)) {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                try (Workbook workbookOut = new XSSFWorkbook()) {
                    xlsxWriter.write(List.of(uncheckedSerialNumbers.toArray()), bos, workbookOut);
                } catch (Exception e) {
                    log.error("Import fm serialNumbers report xls file failed", e);
                }
                return bos.toByteArray();
            } catch (IOException e) {
                log.error("ByteArrayOutputStream ex --- ", e);
                return new byte[0];
            }
        }
        return new byte[0];
    }

    @Override
    @Transactional
    public void importFromExcel(MultipartFile file) {
        List<FiscalModuleReportRequestDTO> records = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();

            for (int i = 1; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null || row.getCell(1) == null || row.getCell(2) == null || row.getCell(3) == null || row.getCell(4) == null || row.getCell(5) == null) {
                    continue;
                }

                FiscalModuleReportRequestDTO fiscalModuleReportRequestDTO = new FiscalModuleReportRequestDTO();
                row.getCell(0).setCellType(CellType.STRING);
                fiscalModuleReportRequestDTO.setCustomerId(customerRepository.getIdByTin(row.getCell(0).getStringCellValue()));
                row.getCell(1).setCellType(CellType.STRING);
                fiscalModuleReportRequestDTO.setDeviceSerialNumber(String.valueOf(row.getCell(1).getStringCellValue()));
                row.getCell(1).setCellType(CellType.STRING);
                fiscalModuleReportRequestDTO.setFmSerialNumber(row.getCell(2).getStringCellValue());
                fiscalModuleReportRequestDTO.setReason(FMReportReason.get(row.getCell(3).getStringCellValue().split(" - ")[1].trim()));
                fiscalModuleReportRequestDTO.setStatus(ArrivalStatus.get(row.getCell(4).getStringCellValue().split(" - ")[1].trim()));
                fiscalModuleReportRequestDTO.setSoliqTransferStatus(SoliqTransferStatus.get(row.getCell(5).getStringCellValue().split(" - ")[1].trim()));
                records.add(fiscalModuleReportRequestDTO);
            }
        } catch (Exception e) {
            log.error("Failed to read Fiscal Module file", e);
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_UNEXPECTED_ERROR).get();
        }
        try {
            for (FiscalModuleReportRequestDTO fiscalModuleReportRequestDTO : records) {
                create(fiscalModuleReportRequestDTO);
            }
        } catch (Exception e) {
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_UNEXPECTED_ERROR).get();
        }
    }

    private Boolean getClassFieldValue(Cases cases) {
        try {
            Class<?> clazz = Class.forName(cases.getCaseType().getClassName());
            Object obj = convertToDto(cases.getJsonProperties(), clazz);
            Field f = obj.getClass().getDeclaredField("lossFiscalModule");
            f.setAccessible(true);
            return Boolean.parseBoolean(f.get(obj).toString());
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String getClassFieldValue(Cases cases, String fieldName) {
        try {
            Class<?> clazz = Class.forName(cases.getCaseType().getClassName());
            Object obj = convertToDto(cases.getJsonProperties(), clazz);
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj).toString();
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T convertToDto(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
