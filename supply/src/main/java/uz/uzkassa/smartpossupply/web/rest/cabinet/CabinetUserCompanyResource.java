package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.auth.TokenDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.user.UserCompanyAuthDTO;
import uz.uzkassa.smartpossupply.service.UserCompanyService;

import java.util.List;

@RequestMapping(ApiConstants.cabinetUserCompanyRootApi)
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetUserCompanyResource {

    UserCompanyService userCompanyService;

    @PostMapping("/change")
    public ResponseEntity<TokenDTO> change(@Valid @RequestBody UserCompanyAuthDTO dto) {
        return ResponseEntity.ok(userCompanyService.changeCompany(dto));
    }

    @GetMapping
    public ResponseEntity<List<CommonDTO>> getList() {
        return ResponseEntity.ok(userCompanyService.getCurrentUserCompanies());
    }

}
