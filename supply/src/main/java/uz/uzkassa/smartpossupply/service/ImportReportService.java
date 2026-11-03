package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.exportimport.ImportReport;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.importreport.ImportReportListDTO;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportItemPayload;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportPayload;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.ImportReportRepository;
import uz.uzkassa.smartpossupply.service.handlers.excel.importreport.CustomerImportReportExcelHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.importreport.OrderImportReportExcelHandler;
import uz.uzkassa.smartpossupply.service.handlers.excel.importreport.ProductImportReportExcelHandler;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.02.2023 15:00
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class ImportReportService extends BaseService {

    ImportReportRepository importReportRepository;

    CustomerImportReportExcelHandler customerImportReportExcelHandler;
    OrderImportReportExcelHandler orderImportReportExcelHandler;
    ProductImportReportExcelHandler productImportReportExcelHandler;

    @Transactional
    public void importReportQueue(ImportReportPayload payload) {
        switch (payload.getEntityType()) {
            case CUSTOMER:
                customerImportReportExcelHandler.sendImportReportItem(payload);
                break;
            case ORDER:
                orderImportReportExcelHandler.importReportItemQueue(payload);
                break;
            case PRODUCT:
                productImportReportExcelHandler.importReportQueue(payload);
                break;
            default:
        }
    }

    @Transactional
    public void importReportItemQueue(ImportReportItemPayload payload) {
        try {
            customerImportReportExcelHandler.importReportItemQueue(payload);
        } catch (Exception e) {
            log.error("Error in importReportItemQueue: {}", e.getMessage());
        }
    }

    public Page<ImportReportListDTO> getList(BaseFilter filter) {
        ResultList<ImportReport> resultList = importReportRepository.getResultList(filter);
        List<ImportReportListDTO> result = resultList
                .getList()
                .stream()
                .map(importReport -> {
                    ImportReportListDTO importReportListDTO = new ImportReportListDTO();
                    importReportListDTO.setId(importReport.getId());
                    if (importReport.getFile() != null) {
                        importReportListDTO.setFile(importReport.getFile().toFileDTO());
                    }
                    importReportListDTO.setStatus(importReport.getStatus());
                    importReportListDTO.setEntityType(importReport.getEntityType());
                    importReportListDTO.setFileType(importReport.getFileType());
                    importReportListDTO.setCreatedDate(importReport.getCreatedDate());
                    importReportListDTO.setItemTotal(importReport.getItemTotal());
                    importReportListDTO.setImportedTotal(importReport.getImportedTotal());
                    importReportListDTO.setErrorTotal(importReport.getErrorTotal());
                    return importReportListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public ResponseEntity<Resource> downloadErrorFile(Long id) {
        return importReportRepository.findById(id)
                .map(importReport ->
                        customerImportReportExcelHandler.
                                generateErrorFile(importReport.getItems())
                )
                .orElseThrow(validationService.entityNotFoundExceptionThrow(ImportReport.class.getSimpleName(), "id", id));
    }
}
