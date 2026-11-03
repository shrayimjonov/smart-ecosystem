package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.act.AcceptanceTransferActDetailDTO;
import uz.uzkassa.silen.dto.act.AcceptanceTransferActListDTO;
import uz.uzkassa.silen.dto.act.AcceptanceTransferActSilenDTO;
import uz.uzkassa.silen.dto.filter.AcceptanceTransferActFilter;
import uz.uzkassa.silen.enumeration.ActStatus;
import uz.uzkassa.silen.service.AcceptanceTransferActService;

import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Aggregation}.
 */
@RestController
@RequestMapping("/api/acceptance-transfer-act")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AcceptanceTransferActResource {
    AcceptanceTransferActService acceptanceTransferActService;

    @PostMapping
    public ResponseEntity<AcceptanceTransferActDetailDTO> create(@RequestBody @Valid AcceptanceTransferActSilenDTO dto) {
        return ResponseEntity.ok(acceptanceTransferActService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcceptanceTransferActDetailDTO> update(@PathVariable("id") String id, @RequestBody @Valid AcceptanceTransferActSilenDTO aggregationUpdateDTO) {
        return ResponseEntity.ok(acceptanceTransferActService.update(id, aggregationUpdateDTO));
    }

    @PutMapping("/{id}/{status}")
    public ResponseEntity<Void> updateStatus(@PathVariable("id") String id, @PathVariable("status") ActStatus status) {
        acceptanceTransferActService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<AcceptanceTransferActListDTO>> findAllByFilter(@ModelAttribute AcceptanceTransferActFilter filter) {
        return ResponseEntity.ok(acceptanceTransferActService.findAllByFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcceptanceTransferActDetailDTO> get(@PathVariable String id) {
        return ResponseEntity.ok(acceptanceTransferActService.findById(id));
    }

    @GetMapping("/items")
    public ResponseEntity<List<SelectItem>> get(@ModelAttribute AcceptanceTransferActFilter filter) {
        return ResponseEntity.ok(acceptanceTransferActService.getItems(filter));
    }

    @GetMapping("/customer/branches/{tin}")
    public ResponseEntity<Object> getCustomerBranches(@PathVariable("tin") String tin) {
        return ResponseEntity.ok(acceptanceTransferActService.getCustomerBranches(tin));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<SelectItem>> getActStatuses() {
        return ResponseEntity.ok(ActStatus.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAct(@PathVariable("id") String id) {
        acceptanceTransferActService.delete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Void> deleteActProductItem(@PathVariable("itemId") String productItemId) {
        acceptanceTransferActService.removeProductItem(productItemId);
        return ResponseEntity.ok().build();
    }
}
