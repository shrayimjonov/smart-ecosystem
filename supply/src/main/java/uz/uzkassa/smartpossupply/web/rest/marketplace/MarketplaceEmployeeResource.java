package uz.uzkassa.smartpossupply.web.rest.marketplace;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.auth.BaseActivationDTO;
import uz.uzkassa.smartpossupply.dto.auth.BaseForgotPasswordDTO;
import uz.uzkassa.smartpossupply.dto.employee.marketplace.MarketplaceEmployeeDTO;
import uz.uzkassa.smartpossupply.dto.employee.marketplace.MarketplaceEmployeeListDTO;
import uz.uzkassa.smartpossupply.dto.user.SecretKeyDTO;
import uz.uzkassa.smartpossupply.service.EmployeeService;
import uz.uzkassa.smartpossupply.service.UserService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.marketplaceEmployeeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MarketplaceEmployeeResource {

    EmployeeService employeeService;

    @GetMapping("/{companyId}")
    public ResponseEntity<List<MarketplaceEmployeeListDTO>> getAll(@PathVariable Long companyId) {
        return ResponseEntity.ok(employeeService.getForMarketplace(companyId));
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody MarketplaceEmployeeDTO marketplaceEmployeeDTO) {
        return ResponseEntity.ok(employeeService.createMarketplaceEmployee(marketplaceEmployeeDTO));
    }

}
