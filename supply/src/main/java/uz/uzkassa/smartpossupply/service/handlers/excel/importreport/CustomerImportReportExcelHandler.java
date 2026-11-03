package uz.uzkassa.smartpossupply.service.handlers.excel.importreport;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.FileConstants;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.exportimport.ImportReport;
import uz.uzkassa.smartpossupply.domain.exportimport.ImportReportItem;
import uz.uzkassa.smartpossupply.dto.address.AddressDTO;
import uz.uzkassa.smartpossupply.dto.company.CheckTinResultDTO;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerDTO;
import uz.uzkassa.smartpossupply.dto.importreport.CustomerImportReportDTO;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportItemPayload;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportPayload;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.service.CompanyService;
import uz.uzkassa.smartpossupply.service.CustomerService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.03.2023 14:46
 */

@Component
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerImportReportExcelHandler extends AbstractImportReportExcelHandler<CustomerImportReportDTO> {

    CustomerService customerService;
    CompanyService companyService;

    public CustomerImportReportExcelHandler(CustomerService customerService, CompanyService companyService) {
        this.customerService = customerService;
        this.companyService = companyService;
    }


    public void importReport(MultipartFile file, EntityTypeEnum entityType, FileType fileType, CustomerImportReportDTO importReportDTO) {
        if (importReportDTO == null) {
            importReportDTO = new CustomerImportReportDTO();
        }
        importReportDTO.setCompanyId(Optional.ofNullable(importReportDTO.getCompanyId()).orElse(SecurityUtils.getCurrentCompanyId()));
        if (importReportDTO.getCompanyId() == null) {
            throw new BadRequestException("Company id is null");
        }
        List<ImportReportItemPayload> customers = this.readFile(file, importReportDTO);
        FileUpload fileUpload = fileUploadService.fileUpload(file);
        ImportReport importReport = new ImportReport();
        importReport.setEntityType(entityType);
        importReport.setFileType(fileType);
        importReport.setCompanyId(importReportDTO.getCompanyId());
        importReport.setFileId(fileUpload.getId());
        importReport.setItemTotal(customers.size());
        importReportRepository.save(importReport);
        rabbitMqImportReportProducer.send(new ImportReportPayload(importReport.getId(), customers, entityType));
    }

    public List<ImportReportItemPayload> readFile(MultipartFile file, CustomerImportReportDTO importReportDTO) {
        List<ImportReportItemPayload> customers = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows() - 1;
            for (int i = 0; i < rowCount; i++) {
                ImportReportItemPayload importReportItemPayload = new ImportReportItemPayload();
                int counter = 0;
                XSSFRow row = sheet.getRow(i + 1);
                if (row != null && row.getCell(counter) != null && row.getCell(counter).getRawValue() != null && !row.getCell(counter).getRawValue().isEmpty()) {
                    row.getCell(counter).setCellType(CellType.STRING);
                    if (!row.getCell(counter).getStringCellValue().isEmpty()) {
                        importReportItemPayload.addCellValue(CUSTOMER.TIN, row.getCell(counter).getStringCellValue().trim());
                    }
                }
                counter += 1;
                if (row != null && row.getCell(counter) != null && row.getCell(counter).getRawValue() != null && !row.getCell(counter).getRawValue().isEmpty()) {
                    row.getCell(counter).setCellType(CellType.STRING);
                    if (!row.getCell(counter).getStringCellValue().isEmpty()) {
                        importReportItemPayload.addCellValue(CUSTOMER.NAME, row.getCell(counter).getStringCellValue().trim());
                    }
                }
                counter += 1;
                if (row != null && row.getCell(counter) != null && row.getCell(counter).getRawValue() != null && !row.getCell(counter).getRawValue().isEmpty()) {
                    row.getCell(counter).setCellType(CellType.STRING);
                    if (!row.getCell(counter).getStringCellValue().isEmpty()) {
                        importReportItemPayload.addCellValue(CUSTOMER.BRAND, row.getCell(counter).getStringCellValue().trim());
                    }
                }

                counter += 1;
                if (row != null && row.getCell(counter) != null && row.getCell(counter).getRawValue() != null && !row.getCell(counter).getRawValue().isEmpty()) {
                    row.getCell(counter).setCellType(CellType.STRING);
                    if (!row.getCell(counter).getStringCellValue().isEmpty()) {
                        importReportItemPayload.addCellValue(CUSTOMER.PHONE, row.getCell(counter).getStringCellValue().trim());
                    }
                }
                counter += 1;
                if (row != null && row.getCell(counter) != null && row.getCell(counter).getRawValue() != null && !row.getCell(counter).getRawValue().isEmpty()) {
                    row.getCell(counter).setCellType(CellType.STRING);
                    if (!row.getCell(counter).getStringCellValue().isEmpty()) {
                        importReportItemPayload.addCellValue(CUSTOMER.ADDRESS, row.getCell(counter).getStringCellValue().trim());
                    }
                }
                counter += 1;
                if (row != null && row.getCell(counter) != null && row.getCell(counter).getRawValue() != null && !row.getCell(counter).getRawValue().isEmpty()) {
                    row.getCell(counter).setCellType(CellType.STRING);
                    if (!row.getCell(counter).getStringCellValue().isEmpty()) {
                        importReportItemPayload.addCellValue(CUSTOMER.LONGITUDE_LATITUDE, row.getCell(counter).getStringCellValue().trim());
                    }
                }
                if (!importReportItemPayload.getCellValueMap().isEmpty()) {
                    customers.add(importReportItemPayload);
                }
            }
        } catch (IOException e) {
            throw new BadRequestException("Не удалось загрузить файл");
        }
        return customers;
    }

    public void sendImportReportItem(ImportReportPayload importReportPayload) {
        for (ImportReportItemPayload entity : importReportPayload.getEntities()) {
            entity.setImportReportId(importReportPayload.getImportReportId());
            rabbitMqImportReportProducer.sendItems(entity);
        }
    }

    public void importReportItemQueue(ImportReportItemPayload payload) {
        ImportReport importReport = importReportRepository.findById(payload.getImportReportId()).orElse(null);
        if (importReport != null) {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCompanyId(importReport.getCompanyId());
            customerDTO.setCreateDefault(true);
            if (payload.getCellValueMap().get(CUSTOMER.TIN) != null) {
                String tin = payload.getCellValueMap().get(CUSTOMER.TIN).toString().trim().replaceAll(" ", "");
                if (StringUtils.isNotEmpty(tin)) {
                    try {
                        CheckTinResultDTO checkTinResultDTO = null;
                        try {
                            checkTinResultDTO = companyService.checkTin(tin);
                        } catch (Exception e) {
//                                    log.info("Check company error: {}", e.getMessage());
                        }
                        if (checkTinResultDTO != null) {
                            customerDTO.setTin(tin.trim());
                            customerDTO.setName(checkTinResultDTO.getName());
                            AddressDTO addressDTO = new AddressDTO();
                            if (checkTinResultDTO.getAddress() != null) {
                                if (checkTinResultDTO.getAddress().getRegion() != null) {
                                    addressDTO.setRegionId(checkTinResultDTO.getAddress().getRegion().getId());
                                }
                                if (checkTinResultDTO.getAddress().getDistrict() != null) {
                                    addressDTO.setDistrictId(checkTinResultDTO.getAddress().getDistrict().getId());
                                }
                                addressDTO.setStreet(checkTinResultDTO.getAddress().getStreet());
                                addressDTO.setHouse(checkTinResultDTO.getAddress().getHouse());
                                addressDTO.setLongitude(checkTinResultDTO.getAddress().getLongitude());
                                addressDTO.setLatitude(checkTinResultDTO.getAddress().getLatitude());
                                customerDTO.setAddress(addressDTO);
                            }
                            if (checkTinResultDTO.getBankDetail() != null) {
                                CompanyBankDTO companyBankDTO = new CompanyBankDTO();
                                companyBankDTO.setId(checkTinResultDTO.getBankDetail().getId());
                                if (checkTinResultDTO.getBankDetail().getBank() != null) {
                                    companyBankDTO.setBankId(checkTinResultDTO.getBankDetail().getBank().getId());
                                }
                                companyBankDTO.setOked(checkTinResultDTO.getBankDetail().getOked());
                                companyBankDTO.setMain(checkTinResultDTO.getBankDetail().getMain());
                                companyBankDTO.setAccountNumber(checkTinResultDTO.getBankDetail().getAccountNumber());
                                customerDTO.setBank(companyBankDTO);
                            }
                            try {
                                customerService.createOrGet(customerDTO);
                                importReport.setImportedTotal(importReport.getImportedTotal() + 1);
                            } catch (Exception e) {
                                log.error("Create customer error: {}", e.getMessage());
                                ImportReportItem importReportItem = new ImportReportItem();
                                importReportItem.setCellValue1(tin);
                                if (StringUtils.isNotEmpty(e.getMessage())) {
                                    importReportItem.setErrorMessage(e.getMessage());
                                }
                                importReportItem.setImportReportId(importReport.getId());
                                importReportItemRepository.save(importReportItem);
                                importReport.setErrorTotal(importReport.getErrorTotal() + 1);
                                importReportRepository.save(importReport);
                            }
                        }
                    } catch (Exception e) {
                        ImportReportItem importReportItem = new ImportReportItem();
                        importReportItem.setCellValue1(tin);
                        if (StringUtils.isNotEmpty(e.getMessage())) {
                            importReportItem.setErrorMessage(e.getMessage());
                        }
                        importReportItem.setImportReportId(importReport.getId());
                        importReportItemRepository.save(importReportItem);
                        importReport.setErrorTotal(importReport.getErrorTotal() + 1);
                        importReportRepository.save(importReport);
                    }
                }
            } else {
                if (payload.getCellValueMap().get(CUSTOMER.NAME) != null) {
                    customerDTO.setName(payload.getCellValueMap().get(CUSTOMER.NAME).toString());
                }
                if (payload.getCellValueMap().get(CUSTOMER.BRAND) != null) {
                    customerDTO.setBrand(payload.getCellValueMap().get(CUSTOMER.BRAND).toString());
                }
                if (payload.getCellValueMap().get(CUSTOMER.PHONE) != null) {
                    customerDTO.setPhone(payload.getCellValueMap().get(CUSTOMER.PHONE).toString());
                }
                AddressDTO addressDTO = new AddressDTO();
                if (payload.getCellValueMap().get(CUSTOMER.LONGITUDE_LATITUDE) != null || payload.getCellValueMap().get(CUSTOMER.ADDRESS) != null) {
                    if (payload.getCellValueMap().get(CUSTOMER.ADDRESS) != null) {
                        addressDTO.setStreet(payload.getCellValueMap().get(CUSTOMER.ADDRESS).toString());
                    }
                    if (payload.getCellValueMap().get(CUSTOMER.LONGITUDE_LATITUDE) != null) {
                        addressDTO.setLatitude(payload.getCellValueMap().get(CUSTOMER.LONGITUDE_LATITUDE).toString().split(",")[0]);
                        addressDTO.setLongitude(payload.getCellValueMap().get(CUSTOMER.LONGITUDE_LATITUDE).toString().split(",")[1]);
                    }
                    customerDTO.setAddress(addressDTO);
                }
                try {
                    customerService.createOrGet(customerDTO);
                    importReport.setImportedTotal(importReport.getImportedTotal() + 1);
                    importReportRepository.save(importReport);
                } catch (Exception e) {
                    log.error("Customer create error: {}", e.getMessage());
                    ImportReportItem importReportItem = new ImportReportItem();
                    importReportItem.setCellValue1(customerDTO.getName());
                    if (StringUtils.isNotEmpty(e.getMessage())) {
                        importReportItem.setErrorMessage(e.getMessage());
                    }
                    importReportItem.setImportReportId(importReport.getId());
                    importReportItemRepository.save(importReportItem);
                    importReport.setErrorTotal(importReport.getErrorTotal() + 1);
                    importReportRepository.save(importReport);
                }
            }
        }

    }

    @Override
    public ResponseEntity<Resource> generateErrorFile(Object entities) {
        XSSFWorkbook xssfWorkbook = getXSSFWorkbook(getErrorFileName(), getErrorFileHeaders());
        Sheet sheet = xssfWorkbook.getSheet(getErrorFileName());
        int rowIndex = 0;
        uz.uzkassa.smartpossupply.dto.customer.CustomerImportReportDTO[] customers = objectMapper.convertValue(entities, uz.uzkassa.smartpossupply.dto.customer.CustomerImportReportDTO[].class);
        for (uz.uzkassa.smartpossupply.dto.customer.CustomerImportReportDTO customer : customers) {
            Row row = sheet.createRow(++rowIndex);
            row.createCell(0).setCellValue(customer.getCellValue1());
        }
        try {
            Resource resource = new ByteArrayResource(getResource(xssfWorkbook));
            return ResponseEntity
                    .ok()
                    .header("Content-Disposition", "attachment; filename=" + URLEncoder.encode(getErrorFileName(), StandardCharsets.UTF_8) + ".xlsx")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.parseMediaType(FileConstants.CONTENT_TYPE_FOR_EXCEL))
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    List<String> getErrorFileHeaders() {
        return Collections.singletonList(
                "ИНН"
        );
    }

    @Override
    String getErrorFileName() {
        return "ErrorCustomers";
    }
}
