package uz.uzkassa.silen.rest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.dto.ChangelogDTO;
import uz.uzkassa.silen.dto.filter.ChangelogFilter;
import uz.uzkassa.silen.service.ChangelogService;

import java.util.List;

@RestController
@RequestMapping("/api/changelog")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChangelogResource {
    private ChangelogService changelogService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ChangelogDTO changelogDTO) {
        return ResponseEntity.ok(changelogService.create(changelogDTO));
    }

    @GetMapping("/items")
    public ResponseEntity<List<ChangelogDTO>> findAll(@ModelAttribute ChangelogFilter filter) {
        return ResponseEntity.ok(changelogService.findAllByFilter(filter).getContent());
    }

    @GetMapping
    public ResponseEntity<Page<ChangelogDTO>> findAllByFilter(@ModelAttribute ChangelogFilter filter) {
        return ResponseEntity.ok(changelogService.findAllByFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChangelogDTO> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(changelogService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChangelogDTO> update(@PathVariable("id") Long id, @RequestBody ChangelogDTO changelogDTO) {
        return ResponseEntity.ok(changelogService.update(id, changelogDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        changelogService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(changelogService.upload(file));
    }

    @PostMapping(value = "/download", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> downloadImage(@RequestParam("file") String file) {
        return ResponseEntity.ok(changelogService.download(file));
    }
}
