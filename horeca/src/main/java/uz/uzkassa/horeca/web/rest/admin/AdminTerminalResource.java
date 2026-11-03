package uz.uzkassa.horeca.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.sync.FiscalizationSyncDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalAdminDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalListAdminDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.TerminalService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.05.2024
 */
@RestController
@RequestMapping(ApiConstants.adminTerminalRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminTerminalResource {

    TerminalService terminalService;

    @GetMapping
    public ResponseEntity<Page<TerminalListAdminDTO>> getList(BaseFilter filter) {
        return ResponseEntity.ok(terminalService.getList(filter));
    }

    @PostMapping(ApiConstants.sync)
    public ResponseEntity<Long> sync(@RequestBody FiscalizationSyncDTO fiscalizationSyncDTO) {
        return ResponseEntity.ok(terminalService.sync(fiscalizationSyncDTO));
    }

    @GetMapping(ApiConstants.items)
    public ResponseEntity<List<CommonDTO>> getItems(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(terminalService.getAdminItems(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<TerminalAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(terminalService.get(id));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> updateTerminal(@PathVariable Long id, @RequestBody TerminalAdminDTO terminalUpdateDTO) {
        return ResponseEntity.ok(terminalService.update(id, terminalUpdateDTO));
    }
}
