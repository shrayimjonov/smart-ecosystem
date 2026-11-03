package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.PrintTemplateDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.PrintTemplateFilter;
import uz.uzkassa.silen.enumeration.CodeType;
import uz.uzkassa.silen.service.PrintTemplateService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/print/template")
@RequiredArgsConstructor
public class PrintTemplateResource {
    private final PrintTemplateService printTemplateService;

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody PrintTemplateDTO dto) {
        return ResponseEntity.ok(printTemplateService.save(dto));
    }

    @GetMapping
    public ResponseEntity<Page<PrintTemplateDTO>> getAll(@ModelAttribute PrintTemplateFilter filter) {
        return ResponseEntity.ok(printTemplateService.findAllByFilter(filter));
    }

    @GetMapping("/items")
    public ResponseEntity<List<SelectItem>> getItems(@ModelAttribute PrintTemplateFilter filter) {
        return ResponseEntity.ok(printTemplateService.getItems(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrintTemplateDTO> get(@PathVariable String id) {
        return ResponseEntity.ok(printTemplateService.getOne(id));
    }

    @PutMapping
    public ResponseEntity<String> update(@Valid @RequestBody PrintTemplateDTO dto) {
        return ResponseEntity.ok(printTemplateService.save(dto));
    }


    @GetMapping("/types")
    public ResponseEntity<List<SelectItem>> getTypes() {
        return ResponseEntity.ok(Arrays.asList(CodeType.AGGREGATION.toSelectItem(), CodeType.MARK.toSelectItem()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        printTemplateService.delete(id);
        return ResponseEntity.ok().build();
    }
}
