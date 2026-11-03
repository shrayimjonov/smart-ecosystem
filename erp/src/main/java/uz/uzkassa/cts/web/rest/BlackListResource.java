package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.blacklist.request.BlackListRequestDto;
import uz.uzkassa.cts.dto.blacklist.response.BlackListResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.BlackListService;

@RestController
@RequestMapping({"/api/black-list"})
@Tag(name = "Black list")
@Slf4j
@Deprecated
public class BlackListResource {

    private final BlackListService service;

    public BlackListResource(BlackListService service) {
        this.service = service;
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<BlackListResponseDto>> list(BaseFilter filter) {
        log.debug("REST request to get banks list");
        return ResponseEntity.ok(service.list(filter));
    }

    @Operation(summary ="add black list")
    @PostMapping("/add")
    public ResponseEntity<BlackListResponseDto> add(@RequestBody BlackListRequestDto dto) {
        return ResponseEntity.ok(service.add(dto));
    }

    @Operation(summary ="get by id")
    @GetMapping("/{id}")
    public ResponseEntity<BlackListResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary ="get by id")
    @PutMapping("/{id}")
    public ResponseEntity<BlackListResponseDto> updateById(@PathVariable Long id, @RequestBody BlackListRequestDto dto) {
        return ResponseEntity.ok(service.updateById(id, dto));
    }

    @Operation(summary ="get by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
