package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.SmsConstants;
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.domain.DeviceLicense;
import uz.uzkassa.cts.domain.HumoConnect;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.customerbranchdevice.CustomerBranchDeviceRequestDTO;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectLastReceiptRequestDTO;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectOperatorStatusRequestDto;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectStatusRequestDto;
import uz.uzkassa.cts.dto.humoconnect.response.HumoConnectDocFlowStatusExcelResponseDto;
import uz.uzkassa.cts.dto.humoconnect.response.HumoConnectExcelResponseDto;
import uz.uzkassa.cts.dto.humoconnect.response.HumoConnectResponseDto;
import uz.uzkassa.cts.dto.launcher.LauncherTerminalRequestDto;
import uz.uzkassa.cts.dto.smartpos.SmartposLastReceiptResponseDTO;
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.HumoConnectFilter;
import uz.uzkassa.cts.helper.excelhelper.XlsxWriter;
import uz.uzkassa.cts.integrations.SmartposClient;
import uz.uzkassa.cts.repository.DeviceLicenseRepository;
import uz.uzkassa.cts.repository.DeviceRepository;
import uz.uzkassa.cts.repository.HumoConnectRepository;
import uz.uzkassa.cts.service.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class HumoConnectServiceImpl extends BaseServiceImpl<HumoConnect, Long> implements HumoConnectService {

    private final HumoConnectRepository humoConnectRepository;
    private final XlsxWriter xlsxWriter;
    private final DeviceRepository deviceRepository;
    private final CustomerBranchDeviceService customerBranchDeviceService;
    private final DeviceLicenseRepository deviceLicenseRepository;
    private final SmsService smsService;
    private final LauncherService launcherService;
    private final CustomerOldService customerOldService;
    private final SmartposClient smartposClient;

    public HumoConnectServiceImpl(HumoConnectRepository humoConnectRepository,
                                  XlsxWriter xlsxWriter,
                                  DeviceRepository deviceRepository,
                                  CustomerBranchDeviceService customerBranchDeviceService,
                                  DeviceLicenseRepository deviceLicenseRepository,
                                  SmsService smsService,
                                  LauncherService launcherService,
                                  CustomerOldService customerOldService,
                                  SmartposClient smartposClient
    ) {
        super(humoConnectRepository);
        this.humoConnectRepository = humoConnectRepository;
        this.xlsxWriter = xlsxWriter;
        this.deviceRepository = deviceRepository;
        this.customerBranchDeviceService = customerBranchDeviceService;
        this.deviceLicenseRepository = deviceLicenseRepository;
        this.smsService = smsService;
        this.launcherService = launcherService;
        this.customerOldService = customerOldService;
        this.smartposClient = smartposClient;
    }

    @Override
    public Page<HumoConnectResponseDto> list(HumoConnectFilter filter) {
        ResultList<HumoConnectResponseDto> resultList = humoConnectRepository.getResultList(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public void updateStatus(HumoConnectStatusRequestDto dto) {
        HumoConnect humoConnect = findById(dto.getId());
        humoConnect.setStatus(dto.getStatus());
        save(humoConnect);
        if (HumoConnectStatus.READY_TO_CONNECT.equals(dto.getStatus())) {
            customerOldService.attachmentBankTariff(humoConnect.getCustomerId(), humoConnect.getDeviceSerialNumber());
            /*send sms to customer*/
            smsService.send(new EtcRequestDTO(humoConnect.getCustomer().getPhone(), SmsConstants.HUMO_CONNECT_SMS));


            LauncherTerminalRequestDto launcherTerminalRequestDto = new LauncherTerminalRequestDto();
            launcherTerminalRequestDto.setBankType(humoConnect.getBankType().getCode());
            launcherTerminalRequestDto.setTerminalNumbers(Collections.singletonList(humoConnect.getDeviceSerialNumber()));
            launcherService.updateBankType(launcherTerminalRequestDto);
        }
    }

    @Override
    public void updateOperatorStatus(HumoConnectOperatorStatusRequestDto dto) {
        HumoConnect humoConnect = findById(dto.getId());
        if (HumoConnectOperatorStatus.CONNECTED.equals(dto.getOperatorStatus()) && !HumoConnectStatus.READY_TO_CONNECT.equals(humoConnect.getStatus())) {
            throw validationService.badRequestExceptionThrow(STATUS_CANNOT_CHANGE, dto.getOperatorStatus().getName()).get();
        }
        humoConnect.setOperatorStatus(dto.getOperatorStatus());
        if (StringUtils.isNotEmpty(humoConnect.getDeviceSerialNumber()) && HumoConnectOperatorStatus.CONNECTED.equals(dto.getOperatorStatus())) {
            Device device = deviceRepository.findBySerialNumber(humoConnect.getDeviceSerialNumber());
            if (device != null && humoConnect.getCustomerId() != null) {
                CustomerBranchDeviceRequestDTO customerBranchDeviceRequestDTO = new CustomerBranchDeviceRequestDTO();
                customerBranchDeviceRequestDTO.setDeviceId(device.getId());
                customerBranchDeviceRequestDTO.setCustomerId(humoConnect.getCustomerId());
                customerBranchDeviceRequestDTO.setPaymentTypes(Collections.singleton(PaymentType.HUMO));
                customerBranchDeviceRequestDTO.setBankTariffId(humoConnect.getCustomer().getBankTariffId());
                customerBranchDeviceService.changePaymentStatus(customerBranchDeviceRequestDTO);
            }
        }
        save(humoConnect);
    }

    @Override
    public void changeDocFlowStatus(Long id, String docFlowStatus) {
        humoConnectRepository
                .findByCaseId(id)
                .ifPresent(humoConnect -> {
                    humoConnect.setDocFlowStatus(DocFlowStatus.valueOf(docFlowStatus));
                    humoConnectRepository.save(humoConnect);
                });
    }

    @Override
    public byte[] download(HumoConnectFilter filter) {
        List<HumoConnectExcelResponseDto> humoConnectList = humoConnectRepository.getExcelList(filter);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (Workbook workbook = new XSSFWorkbook()) {
                xlsxWriter.write(humoConnectList, bos, workbook);
            } catch (Exception e) {
                log.error("Generating users xls file failed", e);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("ByteArrayOutputStream ex --- ", e);
            return new byte[0];
        }
    }

    @Override
    public void changeLicenseByImportExcel(MultipartFile file) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            List<DeviceLicense> deviceLicenses = new ArrayList<>();
            String serialNumber;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getCell(0) != null) {
                    serialNumber = String.valueOf(row.getCell(0)).toUpperCase().trim();
                } else {
                    continue;
                }
                if (StringUtils.isNotEmpty(serialNumber)) {
                    String finalSerialNumber = serialNumber;
                    deviceLicenseRepository
                            .findByDeviceSerialNumber(serialNumber)
                            .ifPresentOrElse(
                                    (deviceLicense) -> {
                                    },
                                    () -> deviceLicenses.add(DeviceLicense.builder().deviceSerialNumber(finalSerialNumber).build()));
                }
            }
            if (CollectionUtils.isNotEmpty(deviceLicenses)) {
                ListUtils.partition(deviceLicenses, 50).forEach(licenses -> {
                    deviceLicenseRepository.saveAll(licenses);
                    deviceLicenseRepository.flush();
                });
            }
        } catch (Exception e) {
            log.error("Error while reading excel file", e);
        }
    }

    @Override
    public byte[] changeDocFlowStatusByExcel(MultipartFile file, String docFlowStatus) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            List<HumoConnect> humoConnects = new ArrayList<>();
            List<HumoConnectDocFlowStatusExcelResponseDto> controversialCases = new ArrayList<>();
            while (rowIterator.hasNext()) {
                long caseId;
                Row row = rowIterator.next();
                if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.NUMERIC) {
                    caseId = (long) row.getCell(0).getNumericCellValue();
                } else if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.STRING) {
                    caseId = Long.parseLong(row.getCell(0).getStringCellValue().trim());
                } else {
                    continue;
                }
                humoConnectRepository
                        .findByCaseId(caseId)
                        .ifPresentOrElse(humoConnect -> {
                            if (humoConnect.getDocFlowStatus() == DocFlowStatus.valueOf(docFlowStatus)) {
                                controversialCases.add(new HumoConnectDocFlowStatusExcelResponseDto(caseId, "Повторный статус"));
                            }
                            humoConnect.setDocFlowStatus(DocFlowStatus.valueOf(docFlowStatus));
                            humoConnects.add(humoConnect);
                        }, () -> controversialCases.add(new HumoConnectDocFlowStatusExcelResponseDto(caseId, "Нет в таблице")));
            }
            if (CollectionUtils.isNotEmpty(humoConnects)) {
                ListUtils.partition(humoConnects, 50).forEach(connectList -> {
                    humoConnectRepository.saveAll(connectList);
                    humoConnectRepository.flush();
                });
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            xlsxWriter.write(controversialCases, bos, new XSSFWorkbook());
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("Error while working with excel file", e);
            return new byte[0];
        }
    }

    @Override
    public void updateLastReceipt(HumoConnectLastReceiptRequestDTO humoConnectLastReceiptRequestDTO) {
        SmartposLastReceiptResponseDTO smartposLastReceiptResponseDTO = smartposClient.getLastReceipt(humoConnectLastReceiptRequestDTO.getSerialNumber(), humoConnectLastReceiptRequestDTO.getTin(), PaymentType.UZ_CARD_MASTERCARD == humoConnectLastReceiptRequestDTO.getPaymentType() ? "UZCARD" : "HUMO");
        humoConnectRepository.findFirstByDeviceSerialNumberAndCaseTypeOrderByIdDesc(humoConnectLastReceiptRequestDTO.getSerialNumber(), CaseType.HUMO_CONNECTION_CHANGE).ifPresent(humoConnect ->
                Optional.ofNullable(smartposLastReceiptResponseDTO).ifPresent(lastReceipt -> {
                    if (humoConnect.getFirstReceiptDate() == null && humoConnect.getFirstReceiptFiscalUrl() == null) {
                        humoConnect.setFirstReceiptDate(lastReceipt.getReceiptDateTime());
                        humoConnect.setFirstReceiptFiscalUrl(lastReceipt.getFiscalUrl());
                    }
                    humoConnect.setLastReceiptDate(lastReceipt.getReceiptDateTime());
                    humoConnect.setLastReceiptFiscalUrl(lastReceipt.getFiscalUrl());
                    humoConnectRepository.save(humoConnect);
                })
        );
    }
}
