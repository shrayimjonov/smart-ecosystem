package uz.uzkassa.smartpos.web.rest;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.service.BranchService;

import java.util.List;

@RestController
@RequestMapping("/api/management")
@RequiredArgsConstructor
public class AdminManagementResource {
    private final BranchService branchService;

    @GetMapping("/branches/search")
    public List<SelectItem> branchLookUp(@ParameterObject final BaseFilter filter) {
        return branchService.getBranchLookUp(filter);
    }

}
