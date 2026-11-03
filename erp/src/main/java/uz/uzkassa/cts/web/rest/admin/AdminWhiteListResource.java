package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.whitelist.WhiteListDetailDTO;
import uz.uzkassa.cts.dto.whitelist.WhiteListListDTO;
import uz.uzkassa.cts.dto.whitelist.WhiteListNewDTO;
import uz.uzkassa.cts.enums.WhiteListAction;
import uz.uzkassa.cts.filter.WhiteListFilter;
import uz.uzkassa.cts.service.WhiteListService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminWhiteListRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminWhiteListResource {

    WhiteListService whiteListService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody WhiteListNewDTO whiteListNewDTO) {
        return ResponseEntity.ok(whiteListService.create(whiteListNewDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody WhiteListNewDTO whiteListNewDTO) {
        return ResponseEntity.ok(whiteListService.update(id, whiteListNewDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<WhiteListDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(whiteListService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<WhiteListListDTO>> getList(@ParameterObject WhiteListFilter filter) {
        return ResponseEntity.ok(whiteListService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        whiteListService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/actions")
    public ResponseEntity<EnumSet<WhiteListAction>> getActions() {
        return ResponseEntity.ok(EnumSet.allOf(WhiteListAction.class));
    }
}
