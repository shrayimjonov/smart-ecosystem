package uz.uzkassa.smartpos.web.rest.terminal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.terminal.company.TerminalCompanyDto;
import uz.uzkassa.smartpos.dto.terminal.company.TuronInfoDto;
import uz.uzkassa.smartpos.service.CompanyService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/terminals/company")
public class TerminalCompanyResource {
    private final CompanyService companyService;

    @GetMapping("/current")
    public TerminalCompanyDto getCurrent() {
        return companyService.currentCompanyForTerminal();
    }

    @GetMapping("/turon/info")
    public TuronInfoDto getTuronInfo() {
        return companyService.getTuronInfo();
    }
}
