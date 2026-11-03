package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.branch.CommonStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.terminal.TerminalServiceDTO;
import uz.uzkassa.smartpossupply.dto.terminal.TerminalListDTO;
import uz.uzkassa.smartpossupply.filters.TerminalFilter;
import uz.uzkassa.smartpossupply.service.TerminalService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.12.2024
 */
@RestController
@RequestMapping(ApiConstants.cabinetTerminalRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetTerminalResource {

    TerminalService terminalService;

    @GetMapping
    public ResponseEntity<Page<TerminalServiceDTO>> getList(@ParameterObject TerminalFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>()));
        }
        return ResponseEntity.ok(terminalService.getTerminals(filter));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<CommonStatisticsDTO> getStatistics(@ParameterObject TerminalFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new CommonStatisticsDTO());
        }
        return ResponseEntity.ok(terminalService.getStatistics(filter));
    }

    @GetMapping("/for-services")
    public ResponseEntity<Page<TerminalServiceDTO>> getTerminals(@ParameterObject TerminalFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>()));
        }
        return ResponseEntity.ok(terminalService.getTerminals(filter));
    }
}