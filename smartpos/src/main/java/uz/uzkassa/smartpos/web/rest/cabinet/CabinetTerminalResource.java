package uz.uzkassa.smartpos.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.CabinetTerminalDto;
import uz.uzkassa.smartpos.dto.filter.CabinetTerminalFilter;
import uz.uzkassa.smartpos.service.TerminalService;

@RequestMapping("/api/v1/cabinet/terminals")
@RestController
@RequiredArgsConstructor
public class CabinetTerminalResource {
    private final TerminalService terminalService;

    @GetMapping
    public Page<CabinetTerminalDto> findAll(CabinetTerminalFilter terminalFilter) {
        return terminalService.findAllForCabinet(terminalFilter);
    }
}
