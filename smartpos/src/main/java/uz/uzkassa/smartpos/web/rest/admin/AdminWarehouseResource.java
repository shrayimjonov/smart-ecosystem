package uz.uzkassa.smartpos.web.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.analytics.RegisterStatsDTO;
import uz.uzkassa.smartpos.service.RegisterService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/warehouse")
public class AdminWarehouseResource {
    private final RegisterService registerService;

    @GetMapping("/stats/{companyId}")
    public RegisterStatsDTO getStats(@PathVariable Long companyId) {
        return registerService.getStatsForAdmin(companyId);
    }
}


