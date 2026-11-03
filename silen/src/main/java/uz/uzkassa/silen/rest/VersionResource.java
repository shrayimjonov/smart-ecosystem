package uz.uzkassa.silen.rest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.VersionDTO;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.service.VersionService;

import java.util.List;

@RestController
@RequestMapping("/api/version")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VersionResource {
    VersionService versionService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody VersionDTO versionDTO) {
        return ResponseEntity.ok(versionService.create(versionDTO));
    }

    @GetMapping("/items")
    public ResponseEntity<List<VersionDTO>> findAll(@ModelAttribute Filter filter) {
        return ResponseEntity.ok(versionService.findAllByFilter(filter).getContent());
    }

    @GetMapping
    public ResponseEntity<Page<VersionDTO>> findAllByFilter(@ModelAttribute Filter filter) {
        return ResponseEntity.ok(versionService.findAllByFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VersionDTO> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(versionService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VersionDTO> update(@PathVariable("id") Long id, @RequestBody VersionDTO versionDTO) {
        return ResponseEntity.ok(versionService.update(id, versionDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        versionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
