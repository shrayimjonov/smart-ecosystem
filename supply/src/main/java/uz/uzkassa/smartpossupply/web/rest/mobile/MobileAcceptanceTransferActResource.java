package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActDTO;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActListDTO;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActLookUpDTO;
import uz.uzkassa.smartpossupply.enums.AcceptanceTransferActStatus;
import uz.uzkassa.smartpossupply.filters.AcceptanceTransferActFilter;
import uz.uzkassa.smartpossupply.service.AcceptanceTransferActService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.mobileAcceptanceTransferActRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileAcceptanceTransferActResource {

    AcceptanceTransferActService acceptanceTransferActService;

    @Operation(summary = ApiConstants.Create, operationId = "createAcceptanceTransferAct")
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody AcceptanceTransferActDTO acceptanceTransferActDTO) {
        return ResponseEntity.ok(acceptanceTransferActService.create(acceptanceTransferActDTO));
    }

    @Operation(summary = ApiConstants.Create, operationId = "createAcceptanceTransferActByStockOut")
    @PostMapping("/stock/{stockOutId}")
    public ResponseEntity<Long> createByStockOut(@PathVariable Long stockOutId) {
        return ResponseEntity.ok(acceptanceTransferActService.createByStockOut(stockOutId));
    }

    @Operation(summary = ApiConstants.Update, operationId = "updateAcceptanceTransferAct")
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody AcceptanceTransferActDTO acceptanceTransferActDTO) {
        return ResponseEntity.ok(acceptanceTransferActService.update(id, acceptanceTransferActDTO));
    }

    @Operation(summary = ApiConstants.Get, operationId = "getAcceptanceTransferAct")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<AcceptanceTransferActDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(acceptanceTransferActService.get(id));
    }

    @Operation(summary = ApiConstants.List, operationId = "getAcceptanceTransferActList")
    @GetMapping
    public ResponseEntity<Page<AcceptanceTransferActListDTO>> getList(@ParameterObject AcceptanceTransferActFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(acceptanceTransferActService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp, operationId = "getAcceptanceTransferActLookUp")
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<AcceptanceTransferActLookUpDTO>> lookUp(@ParameterObject AcceptanceTransferActFilter filter) {
        return ResponseEntity.ok(acceptanceTransferActService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.UpdateStatus, operationId = "updateAcceptanceTransferActStatus")
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") AcceptanceTransferActStatus status) {
        acceptanceTransferActService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Delete, operationId = "deleteAcceptanceTransferAct")
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        acceptanceTransferActService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Statuses, operationId = "getAcceptanceTransferActStatuses")
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<AcceptanceTransferActStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(AcceptanceTransferActStatus.class));
    }
}
