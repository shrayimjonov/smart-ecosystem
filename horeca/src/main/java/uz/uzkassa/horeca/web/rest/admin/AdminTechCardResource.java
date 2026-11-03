package uz.uzkassa.horeca.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.techcard.TechCardDTO;
import uz.uzkassa.horeca.dto.techcard.TechCardDetailDTO;
import uz.uzkassa.horeca.dto.techcard.TechCardListDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.interfaces.CrudRest;
import uz.uzkassa.horeca.service.TechCardService;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminTechCardRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminTechCardResource implements CrudRest<Long, TechCardDTO, TechCardDetailDTO, TechCardListDTO, BaseFilter> {

    TechCardService techCardService;

    @PostMapping
    @Override
    public ResponseEntity<Long> create(@RequestBody TechCardDTO techCardDTO) {
        return ResponseEntity.ok(techCardService.create(techCardDTO));
    }

    @PostMapping(ApiConstants.batch)
    public ResponseEntity<Void> batchCreate(@RequestBody List<TechCardDTO> techCardDTOList) {
        techCardService.batchCreate(techCardDTOList);
        return ResponseEntity.ok().build();
    }

    @PutMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody TechCardDTO techCardDTO) {
        return ResponseEntity.ok(techCardService.update(id, techCardDTO));
    }

    @PutMapping(ApiConstants.batch)
    public ResponseEntity<Void> batchUpdate(@RequestBody List<TechCardDTO> techCardDTOList) {
        techCardService.batchUpdate(techCardDTOList);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping(ApiConstants.id)
    public ResponseEntity<TechCardDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(techCardService.get(id));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<TechCardListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(techCardService.getList(filter));
    }

    @GetMapping("/items/{productId}")
    public ResponseEntity<List<CommonDTO>> getItems(@PathVariable Long productId) {
        return ResponseEntity.ok(techCardService.getItems(productId));
    }

    @Override
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) LocalDateTime lastModifiedDate) {
        techCardService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<TechCardDetailDTO>> getListByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(techCardService.getListByProductId(productId));
    }
}
