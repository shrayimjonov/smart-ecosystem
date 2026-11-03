package uz.uzkassa.smartpos.web.rest.admin;

import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.OneCCompanyDto;
import uz.uzkassa.smartpos.filter.OneCCompanyFilter;
import uz.uzkassa.smartpos.service.OneCCompanyService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/one-c/company")
public class AdminOneCCompanyResource {
    private final OneCCompanyService service;

    @GetMapping
    public Page<OneCCompanyDto> findAll(@ParameterObject OneCCompanyFilter filter) {
        return service.findAllByFilter(filter);
    }
}
