package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.ofdapplication.OfdApplicationCreateDTO;
import uz.uzkassa.cts.dto.ofdapplication.OfdApplicationMergeDTO;
import uz.uzkassa.cts.dto.ofdapplication.SwapDeviceDTO;
import uz.uzkassa.cts.dto.ofdapplication.SwapFiscalModuleDTO;
import uz.uzkassa.cts.dto.ofdapplication.admin.OfdApplicationListAdminDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdApplicationListDTO;
import uz.uzkassa.cts.enums.ApplicationType;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.OfdStatus;
import uz.uzkassa.cts.filter.OfdApplicationFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.OfdApplicationService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminOfdApplicationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminOfdApplicationResource {

    OfdApplicationService ofdApplicationService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody OfdApplicationCreateDTO ofdApplicationCreateDTO) {
        ofdApplicationService.create(ofdApplicationCreateDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<OfdApplicationListAdminDTO>> getList(@ParameterObject OfdApplicationFilter filter) {
        return ResponseEntity.ok(ofdApplicationService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject OfdApplicationFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.OFD_APPLICATION, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject OfdApplicationFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.OFD_APPLICATION, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ofdApplicationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.sync + "/{customerId}")
    public ResponseEntity<Void> sync(@PathVariable Long customerId) {
        ofdApplicationService.sync(customerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.sync)
    public ResponseEntity<Void> sync() {
        ofdApplicationService.sync();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<String> reject(@RequestParam("file") MultipartFile file,
                                         @PathVariable(value = "id") Long id) {
        ofdApplicationService.rejectWithRecalculation(id, file);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/swap/fiscal/module")
    public ResponseEntity<String> swapFiscalModule(@RequestBody SwapFiscalModuleDTO swapFiscalModuleDTO) {
        ofdApplicationService.swapFiscalModule(swapFiscalModuleDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/swap/device")
    public ResponseEntity<String> swapDevice(@RequestBody SwapDeviceDTO swapDeviceDTO) {
        return ResponseEntity.ok(ofdApplicationService.swapDevice(swapDeviceDTO));
    }

    @PatchMapping("/change-application-type/{id}")
    public ResponseEntity<Void> changeApplicationType(@PathVariable(value = "id") Long id, @RequestParam("applicationType") ApplicationType applicationType) {
        ofdApplicationService.changeApplicationType(id, applicationType);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable(value = "id") Long id, @RequestParam("status") OfdStatus status) {
        ofdApplicationService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.merge + "/{fiscalizationId}")
    public ResponseEntity<List<OfdApplicationListDTO>> getListByFiscalizationId(@PathVariable(value = "fiscalizationId") Long fiscalizationId) {
        return ResponseEntity.ok(ofdApplicationService.getListByFiscalizationId(fiscalizationId));
    }

    @PutMapping(ApiConstants.merge)
    public ResponseEntity<Void> merge(@RequestBody OfdApplicationMergeDTO ofdApplicationMergeDTO) {
        ofdApplicationService.merge(ofdApplicationMergeDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change/address/{id}")
    public ResponseEntity<String> changeAddress(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(ofdApplicationService.changeAddress(id));
    }
}
