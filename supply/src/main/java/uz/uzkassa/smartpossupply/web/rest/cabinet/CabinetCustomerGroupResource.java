package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.customergroup.CustomerGroupDTO;
import uz.uzkassa.smartpossupply.dto.customergroup.CustomerGroupDetailDTO;
import uz.uzkassa.smartpossupply.dto.customergroup.CustomerGroupListDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.CustomerGroupService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCustomerGroupRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCustomerGroupResource {

    CustomerGroupService customerGroupService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CustomerGroupDTO customerGroupDTO) {
        return ResponseEntity.ok(customerGroupService.create(customerGroupDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CustomerGroupDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerGroupService.get(id));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomerGroupDTO customerGroupDTO) {
        return ResponseEntity.ok(customerGroupService.update(id, customerGroupDTO));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        customerGroupService.updateStatus(id, CommonStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<CustomerGroupListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(customerGroupService.getList(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(customerGroupService.lookUp(filter));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<CommonStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerGroupService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
