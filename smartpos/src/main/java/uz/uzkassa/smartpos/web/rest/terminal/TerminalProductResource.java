package uz.uzkassa.smartpos.web.rest.terminal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.base.PageDto;
import uz.uzkassa.smartpos.dto.catalog.ProductCreateDto;
import uz.uzkassa.smartpos.dto.catalog.TerminalProductDto;
import uz.uzkassa.smartpos.filter.TerminalProductFilter;
import uz.uzkassa.smartpos.service.ProductService;

@RestController
@RequestMapping("/api/v1/terminals/products")
@RequiredArgsConstructor
public class TerminalProductResource {
    private final ProductService productService;

    @GetMapping
    public PageDto<TerminalProductDto> findAllByFilter(@ParameterObject final TerminalProductFilter filter) {
        return productService.findAllByFilter(filter);
    }

    @PostMapping
    public Long create(@RequestParam(required = false) final Long branchId, @RequestBody @Valid final ProductCreateDto dto) {
        return productService.createForCabinet(branchId, dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable final Long id, @RequestParam(required = false) final Long branchId, @RequestBody @Valid final ProductCreateDto dto) {
        productService.update(id, branchId, dto);
    }
}

