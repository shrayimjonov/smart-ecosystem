package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsCommonDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsDetailDTO;
import uz.uzkassa.cts.dto.repairticket.RepairTicketSettingsListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.RepairTicketSettingsService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminRepairTicketSettingsRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminRepairTicketSettingsResource {

    RepairTicketSettingsService repairTicketSettingsService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody RepairTicketSettingsDTO repairTicketSettingsDTO) {
        return ResponseEntity.ok(repairTicketSettingsService.create(repairTicketSettingsDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<RepairTicketSettingsDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(repairTicketSettingsService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<RepairTicketSettingsListDTO>> getList(BaseFilter filter) {
        return ResponseEntity.ok(repairTicketSettingsService.getList(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<RepairTicketSettingsCommonDTO>> lookUp(BaseFilter filter) {
        return ResponseEntity.ok(repairTicketSettingsService.lookUp(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        repairTicketSettingsService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
