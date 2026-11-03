package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.EpsServicesCrudDto;
import uz.uzkassa.smartpos.dto.EpsServicesDto;
import uz.uzkassa.smartpos.dto.EpsServicesLookupDto;
import uz.uzkassa.smartpos.enums.EPSMethodType;
import uz.uzkassa.smartpos.enums.EPSServicesStatusEnum;
import uz.uzkassa.smartpos.service.EpsService;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/eps-services")
public class EpsServicesResource {
    private final EpsService epsService;

    @PostMapping
    public EpsServicesDto create(@RequestBody @Valid EpsServicesCrudDto dto) {
        return epsService.create(dto);
    }

    @PutMapping("/{id}")
    public EpsServicesDto update(@PathVariable UUID id, @RequestBody @Valid EpsServicesCrudDto dto) {
        return epsService.update(id, dto);
    }

    @GetMapping("/{id}")
    public EpsServicesDto findById(@PathVariable UUID id) {
        return epsService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        epsService.delete(id);
    }

    @GetMapping
    public List<EpsServicesDto> findAll() {
        return epsService.findAll();
    }

    @GetMapping("/lookup")
    public List<EpsServicesLookupDto> lookup(@RequestParam(required = false) Long branchId, @RequestHeader(value = "branchId", required = false) Long camelCaseBranchId, @RequestHeader(value = "BRANCH_ID", required = false) Long headerBranchId) {
        return epsService.lookup(branchId != null ? branchId : camelCaseBranchId != null ? camelCaseBranchId : headerBranchId);
    }

    @GetMapping("/sync")
    public void sync() {
        epsService.sync();
    }

    @GetMapping("/statuses")
    public EPSServicesStatusEnum[] statuses() {
        return EPSServicesStatusEnum.values();
    }

    @GetMapping("/methods")
    public EPSMethodType[] methodTypes() {
        return EPSMethodType.values();
    }
}
