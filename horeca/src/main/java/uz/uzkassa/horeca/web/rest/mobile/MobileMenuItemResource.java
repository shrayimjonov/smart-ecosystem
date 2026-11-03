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
import uz.uzkassa.horeca.dto.menu.mobile.MenuItemDetailMobileDTO;
import uz.uzkassa.horeca.dto.menu.mobile.MenuItemListMobileDTO;
import uz.uzkassa.horeca.filters.MenuItemFilter;
import uz.uzkassa.horeca.service.MenuItemService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.02.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileMenuItemRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileMenuItemResource {

    MenuItemService menuItemService;

    @Operation(operationId = "menuItemGetDetail")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<MenuItemDetailMobileDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.getForMobile(id));
    }

    @Operation(operationId = "menuItemGetList")
    @GetMapping
    public ResponseEntity<Page<MenuItemListMobileDTO>> getList(@ParameterObject MenuItemFilter filter) {
        return ResponseEntity.ok(menuItemService.getListForMobile(filter));
    }
}
