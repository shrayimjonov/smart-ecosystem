package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.contract.ContractDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractDetailDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractListDTO;
import uz.uzkassa.smartpossupply.dto.contract.ContractLookUpDTO;
import uz.uzkassa.smartpossupply.enums.ContractStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.interfaces.CrudRestAdvanced;
import uz.uzkassa.smartpossupply.service.ContractService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetContractRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetContractResource implements CrudRestAdvanced<Long, ContractDTO, ContractDetailDTO, ContractListDTO, BaseFilter, ContractStatus, ContractLookUpDTO> {

    ContractService contractService;

    @Operation(summary = ApiConstants.List)
    @GetMapping
    @Override
    public ResponseEntity<Page<ContractListDTO>> getList(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(contractService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    @Override
    public ResponseEntity<List<ContractLookUpDTO>> lookUp(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setStatus(ContractStatus.ACCEPTED.getCode());
        return ResponseEntity.ok(contractService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    @Override
    public ResponseEntity<Long> create(@RequestBody ContractDTO contractDTO) {
        return ResponseEntity.ok(contractService.create(contractDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    @Override
    public ResponseEntity<ContractDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.get(id));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ContractDTO contractDTO) {
        return ResponseEntity.ok(contractService.update(id, contractDTO));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    @Override
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") ContractStatus status) {
        contractService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<ContractStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(ContractStatus.class));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contractService.delete(id);
        return ResponseEntity.ok().build();
    }
}
