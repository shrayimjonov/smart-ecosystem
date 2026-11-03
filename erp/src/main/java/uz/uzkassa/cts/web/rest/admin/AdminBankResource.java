package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.dto.bank.BankDTO;
import uz.uzkassa.cts.dto.bank.admin.BankDetailAdminDTO;
import uz.uzkassa.cts.dto.bank.admin.BankListAdminDTO;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.BankService;
import uz.uzkassa.cts.service.ExportReportService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminBankRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminBankResource {

    BankService bankService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody BankDTO bankDTO) {
        return ResponseEntity.ok(bankService.create(bankDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody BankDTO bankDTO) {
        return ResponseEntity.ok(bankService.update(id, bankDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<BankDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(bankService.get(id));
    }

    @GetMapping
    @Cacheable(cacheNames = CacheConstants.BANK_LIST_CACHE)
    public ResponseEntity<Page<BankListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(bankService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    @Cacheable(cacheNames = CacheConstants.BANK_LIST_CACHE)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.BANK, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    @Cacheable(cacheNames = CacheConstants.BANK_LIST_CACHE)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.BANK, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    @Cacheable(cacheNames = CacheConstants.BANK_LOOKUP_CACHE)
    public ResponseEntity<List<CommonCodeDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(bankService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        bankService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.sync)
    public ResponseEntity<Void> sync() {
        bankService.sync();
        return ResponseEntity.ok().build();
    }
}
