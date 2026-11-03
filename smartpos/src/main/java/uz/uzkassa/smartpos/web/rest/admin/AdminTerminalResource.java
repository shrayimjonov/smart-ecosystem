package uz.uzkassa.smartpos.web.rest.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.RemovedTerminalDTO;
import uz.uzkassa.smartpos.dto.TerminalDTO;
import uz.uzkassa.smartpos.dto.TerminalLogDTO;
import uz.uzkassa.smartpos.dto.elasticsearch.TerminalSerialNumberDto;
import uz.uzkassa.smartpos.dto.filter.management.TerminalFilter;
import uz.uzkassa.smartpos.dto.filter.management.TerminalLogFilter;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.service.LocalizationService;
import uz.uzkassa.smartpos.service.TerminalService;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;


@RestController
@RequestMapping("/api/admin/terminals")
@Secured({SMARTPOS_ADMIN, SUPPORT, SALES_MANAGER, SUPPORT_MANAGER, REGION_MANAGER, SALES_DIRECTOR})
@RequiredArgsConstructor
public class AdminTerminalResource {
    private final LocalizationService localizationService;
    private final TerminalService terminalService;

    @GetMapping
    public Page<TerminalDTO> getList(@ParameterObject TerminalFilter filter) {
        return terminalService.getList(filter, true);
    }

    @PostMapping
    public ResponseEntity<TerminalDTO> createTerminal(@Valid @RequestBody TerminalDTO terminalDTO) {
        if (terminalDTO.getId() != null) {
            throw new BadRequestException(localizationService.getMessage("new.entity.cannot.id"));
        }
        if (terminalDTO.getBranchId() == null) {
            throw new BadRequestException(localizationService.getMessage("branch.required"));
        }
        TerminalDTO result = terminalService.saveAdmin(terminalDTO);
        return ResponseEntity.ok().body(result);
    }

    @PutMapping
    public ResponseEntity<TerminalDTO> updateTerminal(@Valid @RequestBody TerminalDTO terminalDTO) {
        if (terminalDTO.getId() == null) {
            throw new BadRequestException(localizationService.getMessage("id.required"));
        }
        if (terminalDTO.getBranchId() == null) {
            throw new BadRequestException(localizationService.getMessage("branch.required"));
        }
        TerminalDTO result = terminalService.saveAdmin(terminalDTO);
        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/removed")
    public void removeFromClient(@RequestBody @Valid RemovedTerminalDTO dto) {
        terminalService.removeFromClient(dto.getSerialNumber());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTerminal(@PathVariable Long id) {
        terminalService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/log")
    public TerminalSerialNumberDto saveTerminalLog(@RequestBody TerminalSerialNumberDto logDTO) {
        return terminalService.saveTerminalLog(logDTO);
    }

    @GetMapping("/log")
    public ResponseEntity<Page<TerminalLogDTO>> getAllTerminalLog(@ParameterObject TerminalLogFilter filter) {
        return ResponseEntity.ok(terminalService.getTerminalLog(filter));
    }

    @DeleteMapping("/log/{id}")
    public void deleteTerminalLog(@PathVariable Long id) {
        terminalService.deleteTerminalLogFromId(id);
    }
}
