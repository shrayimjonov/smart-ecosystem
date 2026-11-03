package uz.uzkassa.horeca.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.menu.MenuDetailDTO;
import uz.uzkassa.horeca.dto.menu.mobile.MenuListMobileDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.MenuService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.mobileMenuRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileMenuResource {

    MenuService menuService;

    @Operation(operationId = "menuGetDetail")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<MenuDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.get(id));
    }

    @Operation(operationId = "menuGetList")
    @GetMapping
    public ResponseEntity<Page<MenuListMobileDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(menuService.getListForMobile(filter));
    }
}
