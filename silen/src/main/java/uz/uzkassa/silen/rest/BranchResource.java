package uz.uzkassa.silen.rest;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.BranchDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.BranchFilter;
import uz.uzkassa.silen.enumeration.BranchStatus;
import uz.uzkassa.silen.service.BranchService;

import java.util.List;

@RestController
@RequestMapping("/api/branch")
@RequiredArgsConstructor
public class BranchResource {
    private final BranchService branchService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody BranchDTO branchDTO) {
        return ResponseEntity.ok(branchService.create(branchDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable String id, @RequestBody BranchDTO branchDTO) {
        return ResponseEntity.ok(branchService.update(id, branchDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable String id, @RequestParam("status") BranchStatus branchStatus) {
        return ResponseEntity.ok(branchService.updateStatus(id, branchStatus));
    }

    @GetMapping
    public ResponseEntity<Page<BranchDTO>> findAllByFilter(@ModelAttribute BranchFilter filter) {
        return ResponseEntity.ok(branchService.findAllByFilter(filter));
    }

    @GetMapping("/items")
    public ResponseEntity<List<SelectItem>> findAll(@ModelAttribute BranchFilter filter) {
        return ResponseEntity.ok(branchService.getItems(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchDTO> get(@PathVariable("id") String id) {
        return ResponseEntity.ok(branchService.get(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        branchService.delete(id);
        return ResponseEntity.ok().build();
    }
}
