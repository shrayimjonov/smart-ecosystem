package uz.uzkassa.smartpos.web.rest.v2;

import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.analytics.RegisterStatsDTO;
import uz.uzkassa.smartpos.dto.filter.RegisterFilter;
import uz.uzkassa.smartpos.dto.register.RegisterFillMarkingsDTO;
import uz.uzkassa.smartpos.dto.warehouse.MarkingDTO;
import uz.uzkassa.smartpos.dto.warehouse.RegisterDto;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.service.LocalizationService;
import uz.uzkassa.smartpos.service.MarkingService;
import uz.uzkassa.smartpos.service.RegisterService;

import java.util.List;

@RestController
@RequestMapping("/api/v2/warehouse/register")
public class RegisterResourceV2 {
    private final RegisterService registerService;

    private final LocalizationService localizationService;
    private final MarkingService markingService;

    public RegisterResourceV2(RegisterService registerService, LocalizationService localizationService, MarkingService markingService) {
        this.registerService = registerService;
        this.localizationService = localizationService;
        this.markingService = markingService;
    }

    @GetMapping
    public Page<RegisterDto> getRegisterByBranchId(@ParameterObject RegisterFilter filter) {
        return registerService.findAllV2(filter);
    }

    @GetMapping("/product")
    public List<RegisterDto> getRegisterByProductId(@ParameterObject RegisterFilter filter) {
        if (filter.getUnitId() == null && StringUtils.isNotBlank(filter.getMxik())) {
            throw new BadRequestException(localizationService.getMessage("product.mxik.required"));
        }
        return registerService.getByProductV2(filter);
    }

    @GetMapping("/stats")
    public RegisterStatsDTO getStats(@ParameterObject RegisterFilter filter) {
        return registerService.getStatsV2(filter);
    }

    @PutMapping("/fill/marking")
    public void fillMarkings(@Valid @RequestBody RegisterFillMarkingsDTO dto) {
        registerService.fillMarkings(dto);
    }

    @GetMapping("/markings/{productId}/{branchId}")
    public List<MarkingDTO> getMarkings(@PathVariable Long branchId, @PathVariable Long productId) {
        return markingService.getMarkingsByProduct(branchId, productId);
    }

    @Secured({AuthoritiesConstants.OWNER, AuthoritiesConstants.SMARTPOS_ADMIN, AuthoritiesConstants.SUPPORT, AuthoritiesConstants.SALES_MANAGER})
    @PutMapping("/reset")
    public void resetWarehouse(Long branchId) {
        registerService.resetWarehouse(branchId);
    }
}
