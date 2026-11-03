package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.branch.CommonStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.integration.TerminalSyncDTO;
import uz.uzkassa.smartpossupply.dto.terminal.TerminalListDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.TerminalFilter;
import uz.uzkassa.smartpossupply.service.TerminalService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.12.2024
 */
@RestController
@RequestMapping(ApiConstants.adminTerminalRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminTerminalResource {

    TerminalService terminalService;

    @GetMapping
    public ResponseEntity<Page<TerminalListDTO>> getList(@ParameterObject TerminalFilter filter) {
        return ResponseEntity.ok(terminalService.getList(filter));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<CommonStatisticsDTO> getStatistics(@ParameterObject TerminalFilter filter) {
        return ResponseEntity.ok(terminalService.getStatistics(filter));
    }

    @PostMapping(ApiConstants.sync)
    public ResponseEntity<Long> sync(@RequestBody TerminalSyncDTO terminalSyncDTO) {
        return ResponseEntity.ok(terminalService.sync(terminalSyncDTO));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Long> updateStatus(@PathVariable Long id,
                                             @RequestParam("status") String status,
                                             @RequestParam(value = "syncWithLauncher", required = false) Boolean syncWithLauncher) {
        terminalService.updateStatus(id, CommonStatus.valueOf(status), syncWithLauncher);
        return ResponseEntity.ok().build();
    }
}