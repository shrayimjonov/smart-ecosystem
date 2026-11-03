package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.report.ContractReport;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.CustomerDeviceDTO;
import uz.uzkassa.cts.dto.payload.ContractReportPayload;
import uz.uzkassa.cts.dto.report.ContractReportDTO;
import uz.uzkassa.cts.dto.report.ContractReportExcelExportDto;
import uz.uzkassa.cts.dto.report.ContractReportListDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.ContractReportFilter;
import uz.uzkassa.cts.helper.excelhelper.XlsxWriter;
import uz.uzkassa.cts.mappings.ContractReportMapper;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.ActOfTransmissionStrategyService;
import uz.uzkassa.cts.service.BillService;
import uz.uzkassa.cts.service.ContractReportService;
import uz.uzkassa.cts.service.PaymentActService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.ValidationUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ContractReportServiceImpl extends BaseServiceImpl<ContractReport, Long> implements ContractReportService {

    private final ContractReportRepository contractReportRepository;
    private final DeviceRepository deviceRepository;
    private final CustomerRepository customerRepository;
    private final RegionRepository regionRepository;
    private final XlsxWriter xlsxWriter;
    private final PaymentActService paymentActService;
    private final CustomerBranchRepository customerBranchRepository;
    private final ContractReportMapper contractReportMapper;
    private final ActOfTransmissionStrategyService actOfTransmissionStrategyService;
    private final BillService billService;

    public ContractReportServiceImpl(ContractReportRepository contractReportRepository,
                                     DeviceRepository deviceRepository,
                                     CustomerRepository customerRepository,
                                     RegionRepository regionRepository,
                                     XlsxWriter xlsxWriter, PaymentActService paymentActService,
                                     CustomerBranchRepository customerBranchRepository,
                                     ContractReportMapper contractReportMapper,
                                     ActOfTransmissionStrategyService actOfTransmissionStrategyService, BillService billService) {
        super(contractReportRepository);
        this.contractReportRepository = contractReportRepository;
        this.deviceRepository = deviceRepository;
        this.customerRepository = customerRepository;
        this.regionRepository = regionRepository;
        this.xlsxWriter = xlsxWriter;
        this.paymentActService = paymentActService;
        this.customerBranchRepository = customerBranchRepository;
        this.contractReportMapper = contractReportMapper;
        this.actOfTransmissionStrategyService = actOfTransmissionStrategyService;
        this.billService = billService;
    }

    @Override
    public Page<ContractReportListDTO> list(ContractReportFilter filter) {
        ResultList<ContractReportListDTO> resultList = contractReportRepository.getResultList(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public ContractReportDTO get(Long id) {
        return contractReportMapper.toDTO(findById(id));
    }

    @Override
    @Transactional
    public Long create(ContractReportDTO contractReportDTO) {
        ContractReport contractReport = contractReportMapper.toEntity(contractReportDTO);
        if (Objects.isNull(deviceRepository.findBySerialNumber(contractReportDTO.getSerialNumber())) || Objects.isNull(customerRepository.findByTin(contractReportDTO.getTin()))) {
            contractReport.setCheckStatus(CheckStatus.WRONG);
        }
        contractReport.setRegionId(contractReportDTO.getRegionId());
        contractReport = contractReportRepository.save(contractReport);
        return contractReport.getId();
    }

    @Override
    @Transactional
    public Long update(ContractReportDTO contractReportDTO) {
        ContractReport contractReport = contractReportRepository.findById(contractReportDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(REPORT));
        if (Objects.isNull(customerRepository.findByTin(contractReportDTO.getTin())))
            contractReport.setCheckStatus(CheckStatus.WRONG);
        if (Objects.nonNull(contractReportDTO.getRegionId())) {
            regionRepository.findById(contractReportDTO.getRegionId()).ifPresent(region -> {
                contractReport.setRegionId(region.getId());
                contractReport.setRegion(region);
            });
        }
        if (ValidationUtil.isNotNull(contractReportDTO.getName())) {
            contractReport.setName(contractReportDTO.getName());
        }
        if (ValidationUtil.isNotNull(contractReportDTO.getContractNumber())) {
            contractReport.setContractNumber(contractReportDTO.getContractNumber());
        }
        if (ValidationUtil.isNotNull(contractReportDTO.getTin())) {
            contractReport.setTin(contractReportDTO.getTin());
        }
        if (contractReportDTO.getAmount() != null) {
            contractReport.setAmount(new BigDecimal(contractReportDTO.getAmount()));
        }
        if (ValidationUtil.isNotNull(contractReportDTO.getDeviceReplacement())) {
            contractReport.setDeviceReplacement(contractReportDTO.getDeviceReplacement());
        }
        if (contractReportDTO.getDate() != null) {
            contractReport.setDate(contractReportDTO.getDate());
        }
        if (contractReportDTO.getDocumentStatus() != null) {
            contractReport.setDocumentStatus(contractReportDTO.getDocumentStatus());
        }
        if (contractReportDTO.getRealisation() != null) {
            contractReport.setRealisation(contractReportDTO.getRealisation());
        }
        if (contractReportDTO.getInvoiceStatus() != null) {
            contractReport.setInvoiceStatus(contractReportDTO.getInvoiceStatus());
        }
        if (contractReportDTO.getSaleStatus() != null) {
            contractReport.setSaleStatus(contractReportDTO.getSaleStatus());
        }
        contractReport.setComment(contractReportDTO.getComment());
        contractReport.setReason(contractReportDTO.getReason());

        save(contractReport);
        return contractReport.getId();
    }

    @Override
    public byte[] exportExcel(ContractReportFilter filter) {
        List<ContractReportExcelExportDto> list = contractReportRepository.exportExcel(filter);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (Workbook workbook = new XSSFWorkbook()) {
                xlsxWriter.write(list, bos, workbook);
            } catch (Exception e) {
                log.error("Generating contract report xls file failed", e);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("ByteArrayOutputStream ex --- ", e);
            return new byte[0];
        }
    }

    @Override
    @Transactional
    public Boolean importExcel(MultipartFile file) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();
            List<ContractReport> contractReports = new ArrayList<>();
            while (rowIterator.hasNext()) {
                ContractReport contractReport;
                Row row = rowIterator.next();
                if (row.getCell(0) != null) {
                    String serialNumber = String.valueOf(row.getCell(0)).toUpperCase();
                    contractReport = contractReportRepository.findBySerialNumber(serialNumber).orElse(new ContractReport());
                    contractReport.setSerialNumber(serialNumber);
                } else
                    continue;
                if (row.getCell(1) != null && row.getCell(1).getCellType() == CellType.NUMERIC) {
                    contractReport.setTin(NumberToTextConverter.toText(row.getCell(1).getNumericCellValue()));
                } else if (row.getCell(1) != null && row.getCell(1).getCellType() == CellType.STRING) {
                    contractReport.setTin(row.getCell(1).getStringCellValue());
                }
                if (row.getCell(2) != null)
                    contractReport.setName(row.getCell(2).getStringCellValue());
                if (row.getCell(3) != null && row.getCell(3).getCellType() == CellType.NUMERIC) {
                    contractReport.setContractNumber(NumberToTextConverter.toText(row.getCell(3).getNumericCellValue()));
                } else if (row.getCell(3) != null && row.getCell(3).getCellType() == CellType.STRING) {
                    contractReport.setContractNumber(row.getCell(3).getStringCellValue());
                }
                if (row.getCell(4) != null) {
                    contractReport.setAmount(new BigDecimal(row.getCell(4).toString().replaceAll("\\p{Z}", "")));
                }
                if (row.getCell(5) != null && StringUtils.isNotEmpty(row.getCell(5).getStringCellValue()))
                    contractReport.setInvoiceStatus(ContractReportInvoiceStatus.valueOf(row.getCell(5).getStringCellValue()));
                if (row.getCell(6) != null && StringUtils.isNotEmpty(row.getCell(6).getStringCellValue()))
                    contractReport.setRealisation(ContractReportPaymentType.valueOf(row.getCell(6).getStringCellValue()));
                if (row.getCell(7) != null && row.getCell(7).getCellType() == CellType.NUMERIC) {
                    Long regionCode = Long.valueOf(new java.text.DecimalFormat("0").format(row.getCell(7).getNumericCellValue()));
                    contractReport.setRegionId(regionRepository.findByCode(regionCode).getId());
                } else if (row.getCell(7) != null && row.getCell(7).getCellType() == CellType.STRING) {
                    Long regionCode = Long.valueOf(row.getCell(7).getStringCellValue());
                    contractReport.setRegionId(regionRepository.findByCode(regionCode).getId());
                }
                if (row.getCell(8) != null)
                    contractReport.setComment(row.getCell(8).getStringCellValue());
                if (row.getCell(9) != null && StringUtils.isNotEmpty(row.getCell(9).getStringCellValue()))
                    contractReport.setDocumentStatus(ActDocFlowStatus.valueOf(row.getCell(9).getStringCellValue()));
                if (row.getCell(10) != null && StringUtils.isNotEmpty(row.getCell(10).getStringCellValue()))
                    contractReport.setSaleStatus(ContractReportInvoiceSaleStatus.valueOf(row.getCell(10).getStringCellValue()));
                else
                    continue;
                if (row.getCell(11) != null)
                    contractReport.setDeviceReplacement(row.getCell(11).getStringCellValue());
                if (row.getCell(12) != null)
                    contractReport.setReason(row.getCell(12).getStringCellValue());
                if (row.getCell(13) != null && row.getCell(13).getCellType() == CellType.NUMERIC) {
                    contractReport.setDate(DateUtil.getStartDate(String.valueOf(row.getCell(13))));
                } else if (row.getCell(13) != null && row.getCell(13).getCellType() == CellType.STRING) {
                    contractReport.setDate(DateUtil.getStartDate(row.getCell(13).getStringCellValue()));
                }
                if (StringUtils.isEmpty(contractReport.getSerialNumber()) || StringUtils.isEmpty(contractReport.getTin())) {
                    contractReport.setCheckStatus(CheckStatus.WRONG);
                } else {
                    contractReport.setCheckStatus(CheckStatus.RIGHT);
                }
                contractReports.add(contractReport);
            }
            contractReportRepository.saveAll(contractReports);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.TRUE;
    }

    @Override
    public void sync() {
        List<ContractReport> list = deviceRepository.findAll().stream().map(device -> {
            ContractReport contractReport = new ContractReport();
            contractReport.setCheckStatus(CheckStatus.WRONG);
            contractReport.setSaleStatus(ContractReportInvoiceSaleStatus.EMPTY);
            contractReport.setSerialNumber(device.getSerialNumber());
            return contractReport;
        }).collect(Collectors.toList());

        contractReportRepository.saveAll(list);
    }

    @Override
    @Transactional
    public void create(ContractReportPayload payload) {
        switch (payload.getEntityType()) {
            case PAYMENT_ACT -> {
                PaymentAct paymentAct = paymentActService.findById(payload.getEntityId());
                ContractReport contractReport = contractReportRepository
                        .findBySerialNumberAndSaleStatusIn(paymentAct.getDevice().getSerialNumber(), List.of(ContractReportInvoiceSaleStatus.EMPTY))
                        .orElse(new ContractReport());
                contractReport.setSerialNumber(paymentAct.getDevice().getSerialNumber());
                contractReport.setTin(paymentAct.getContract().getCustomer().getTin());
                contractReport.setName(paymentAct.getContract().getCustomer().getCompanyName());
                contractReport.setContractNumber(paymentAct.getContract().getNumber());
                contractReport.setRegionId(paymentAct.getContract().getCustomer().getAddress().getRegion().getId());
                contractReport.setDocumentStatus(ActDocFlowStatus.IN_CTS);
                contractReport.setSaleStatus(ContractReportInvoiceSaleStatus.SOLD);
                contractReport.setRealisation(ContractReportPaymentType.FULL_PAYMENT);
                contractReport.setInvoiceStatus(ContractReportInvoiceStatus.ACCEPTED);
                contractReport.setDate(LocalDateTime.now());
                save(contractReport);
            }
            case ACT_OF_TRANSMISSION -> {
                ActOfTransmission actOfTransmission = actOfTransmissionStrategyService.findById(payload.getEntityId());
                ContractForLeasing contract = actOfTransmission.getContract();
                actOfTransmission.getDetails().forEach(actOfTransmissionDetail -> {
                    ContractReport contractReport = contractReportRepository
                            .findBySerialNumberAndSaleStatusIn(actOfTransmissionDetail.getDevice().getSerialNumber(), List.of(ContractReportInvoiceSaleStatus.EMPTY))
                            .orElse(new ContractReport());
                    contractReport.setSerialNumber(actOfTransmissionDetail.getDevice().getSerialNumber());
                    contractReport.setTin(contract.getCustomer().getTin());
                    contractReport.setName(contract.getCustomer().getCompanyName());
                    contractReport.setContractNumber(contract.getNumber());
                    contractReport.setRegionId(contract.getCustomer().getAddress().getRegion().getId());
                    contractReport.setDocumentStatus(ActDocFlowStatus.IN_CTS);
                    contractReport.setSaleStatus(ContractReportInvoiceSaleStatus.SOLD);
                    contractReport.setRealisation(ContractReportPaymentType.FULL_PAYMENT);
                    contractReport.setInvoiceStatus(ContractReportInvoiceStatus.ACCEPTED);
                    contractReport.setDate(LocalDateTime.now());
                    save(contractReport);
                });
            }
            case BILL -> {
                Bill bill = billService.findById(payload.getEntityId());
                ContractReport contractReport = contractReportRepository
                        .findBySerialNumberAndSaleStatusIn(bill.getDevice().getSerialNumber(), List.of(ContractReportInvoiceSaleStatus.EMPTY))
                        .orElse(new ContractReport());
                contractReport.setSerialNumber(bill.getDevice().getSerialNumber());
                contractReport.setTin(bill.getCustomer().getTin());
                contractReport.setName(bill.getCustomer().getCompanyName());
                contractReport.setContractNumber(bill.getId().toString());
                contractReport.setRegionId(bill.getCustomer().getAddress().getRegion().getId());
                contractReport.setDocumentStatus(ActDocFlowStatus.IN_CTS);
                contractReport.setSaleStatus(ContractReportInvoiceSaleStatus.SOLD);
                contractReport.setRealisation(ContractReportPaymentType.FULL_PAYMENT);
                contractReport.setInvoiceStatus(ContractReportInvoiceStatus.ACCEPTED);
                contractReport.setDate(LocalDateTime.now());
                save(contractReport);
            }
            case DEVICE -> {
                Device device = deviceRepository.findById(payload.getEntityId()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
                ContractReport contractReport = contractReportRepository
                        .findBySerialNumberAndSaleStatusIn(device.getSerialNumber(), List.of(ContractReportInvoiceSaleStatus.EMPTY))
                        .orElse(new ContractReport());
                contractReport.setSerialNumber(device.getSerialNumber());
                save(contractReport);
            }
        }
    }

    @Override
    @Transactional
    public void createThroughRegister(CustomerDeviceDTO customerDeviceDTO) {
        Device device = deviceRepository.findById(customerDeviceDTO.getDevices().getFirst()).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
        Optional<ContractReport> contractReportOptional = contractReportRepository.findBySerialNumber(device.getSerialNumber());
        if (contractReportOptional.isEmpty()) {
            Customer customer = customerRepository.findById(customerDeviceDTO.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
            CustomerBranch customerBranch = customerBranchRepository.findById(customerDeviceDTO.getCustomerBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
            ContractReport contractReport = new ContractReport();
            contractReport.setSerialNumber(device.getSerialNumber());
            contractReport.setTin(customer.getTin());
            contractReport.setName(customer.getCompanyName());
            contractReport.setSaleStatus(ContractReportInvoiceSaleStatus.EMPTY);
            contractReport.setCheckStatus(CheckStatus.RIGHT);
            contractReport.setDocumentStatus(ActDocFlowStatus.IN_CTS);
            contractReport.setDate(LocalDateTime.now());

            switch (customerDeviceDTO.getDevicePaymentType()) {
                case LEASING, LEASING_PRIME -> {
                    contractReport.setRealisation(ContractReportPaymentType.valueOf(customerDeviceDTO.getDevicePaymentType().getCode()));
                    contractReport.setContractNumber(PrefixEnum.CL + "/" + customerDeviceDTO.getContractId());
                }
                case STS_TRADE_IN -> {
                    contractReport.setRealisation(ContractReportPaymentType.valueOf(DevicePaymentType.STS_TRADE_IN.getCode()));
                    contractReport.setContractNumber(PrefixEnum.CTI + "/" + customerDeviceDTO.getContractId());
                }
                case FULL_PAYMENT -> {
                    contractReport.setRealisation(ContractReportPaymentType.valueOf(DevicePaymentType.FULL_PAYMENT.getCode()));
                    contractReport.setContractNumber(PrefixEnum.CP + "/" + customerDeviceDTO.getContractId());
                }
                case A_PAY -> {
                    contractReport.setSaleStatus(ContractReportInvoiceSaleStatus.SOLD);
                    contractReport.setRealisation(ContractReportPaymentType.valueOf(DevicePaymentType.A_PAY.getCode()));
                }
                case WHOLESALE_TRADE ->
                        contractReport.setRealisation(ContractReportPaymentType.valueOf(DevicePaymentType.WHOLESALE_TRADE.getCode()));

                case FREE_REPLACEMENT_KKM ->
                        contractReport.setRealisation(ContractReportPaymentType.valueOf(DevicePaymentType.FREE_REPLACEMENT_KKM.getCode()));
            }
            if (customerBranch.getAddress().getRegion() != null) {
                contractReport.setRegionId(customerBranch.getAddress().getRegion().getId());
            }
            save(contractReport);
        }
    }
}
