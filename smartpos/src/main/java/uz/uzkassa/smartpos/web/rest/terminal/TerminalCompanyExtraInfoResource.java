package uz.uzkassa.smartpos.web.rest.terminal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.company.CompanyExtraInfoDTO;
import uz.uzkassa.smartpos.service.CompanyExtraInfoService;

@RestController
@RequestMapping("/api/v1/terminals/companies/extra-info")
@RequiredArgsConstructor
public class TerminalCompanyExtraInfoResource {
    private final CompanyExtraInfoService companyExtraInfoService;

    @GetMapping
    public CompanyExtraInfoDTO extraInfo() {
        return companyExtraInfoService.extraInfo();
    }
}
