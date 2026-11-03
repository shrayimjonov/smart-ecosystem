package uz.uzkassa.horeca.web.rest.terminal;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.menu.MenuItemDetailDTO;
import uz.uzkassa.horeca.dto.menu.MenuItemListDTO;
import uz.uzkassa.horeca.dto.menu.TerminalMenuItemDetailDTO;
import uz.uzkassa.horeca.dto.menu.TerminalMenuItemListDTO;
import uz.uzkassa.horeca.filters.MenuItemFilter;
import uz.uzkassa.horeca.service.MenuItemService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.08.2024
 */
@RestController
@RequestMapping(ApiConstants.terminalMenuItemRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TerminalMenuItemResource {

    MenuItemService menuItemService;

    @GetMapping(ApiConstants.id)
    public ResponseEntity<TerminalMenuItemDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.getForTerminal(id));
    }

    @GetMapping
    public ResponseEntity<Page<TerminalMenuItemListDTO>> getList(@ParameterObject MenuItemFilter filter) {
        return ResponseEntity.ok(menuItemService.getListForTerminal(filter));
    }

    @PatchMapping("/{id}/favourite")
    public ResponseEntity<Void> favourite(@PathVariable Long id) {
        menuItemService.favorite(id);
        return ResponseEntity.ok().build();
    }
}
