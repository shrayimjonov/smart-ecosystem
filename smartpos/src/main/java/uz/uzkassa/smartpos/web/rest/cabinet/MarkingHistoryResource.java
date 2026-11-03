package uz.uzkassa.smartpos.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.SelectItemDTO;
import uz.uzkassa.smartpos.dto.turon.CisInfoWrapper;
import uz.uzkassa.smartpos.dto.turon.PublicCisInfo;
import uz.uzkassa.smartpos.dto.warehouse.*;
import uz.uzkassa.smartpos.enums.AdjustmentReason;
import uz.uzkassa.smartpos.enums.DocumentType;
import uz.uzkassa.smartpos.enums.ProductGroup;
import uz.uzkassa.smartpos.filter.MarkingHistoryFilter;
import uz.uzkassa.smartpos.integration.trueApi.WithdrawResponseDTO;
import uz.uzkassa.smartpos.service.MarkingHistoryService;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cabinet/markings/history")
@Slf4j
@RequiredArgsConstructor
public class MarkingHistoryResource {
    private final MarkingHistoryService markingHistoryService;

    @PostMapping("/adjustment")
    public void create(@Valid @RequestBody MarkingHistoryCreateDto dto) {
        markingHistoryService.create(dto);
    }

    @PutMapping("/adjustment/{id}")
    public void update(@PathVariable Long id, @Valid @RequestBody MarkingHistoryCreateDto dto) {
        markingHistoryService.update(id, dto);
    }

    @PatchMapping("/adjustment/{id}")
    public void updateStatus(@PathVariable Long id) {
        markingHistoryService.updateStatus(id);
    }

    @GetMapping("/adjustment")
    public Page<MarkingHistoryListDto> listAdjustments(@ParameterObject MarkingHistoryFilter filter) {
        return markingHistoryService.listAdjustments(filter);
    }

    @GetMapping("/adjustment/{id}")
    public MarkingHistoryDto findAdjustmentById(@PathVariable Long id) {
        return markingHistoryService.findAdjustmentById(id);
    }

    @DeleteMapping("/adjustment/{id}")
    public void delete(@PathVariable Long id) {
        markingHistoryService.delete(id);
    }

    @PostMapping("/validation")
    public ResponseEntity<List<CisInfoWrapper<PublicCisInfo>>> validation(@Valid @RequestBody MarkingCheckDto dto) {
        return markingHistoryService.validation(dto);
    }

    @GetMapping("/product/groups")
    public List<SelectItemDTO> productGroups() {
        return ProductGroup.getAll();
    }


    @GetMapping("/withdraw/reasons")
    public ResponseEntity<EnumSet<AdjustmentReason>> getAdjustmentReasons() {
        return ResponseEntity.ok(EnumSet.allOf(AdjustmentReason.class));
    }

    @GetMapping("/withdraw/document-types")
    public ResponseEntity<EnumSet<DocumentType>> getDocumentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DocumentType.class));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponseDTO> withdraw(@RequestParam(required = false) Long companyId, @RequestBody @Valid WithdrawRequestDTO withdrawRequestDTO) {
        return ResponseEntity.ok(markingHistoryService.withdraw(companyId, withdrawRequestDTO));
    }
}
