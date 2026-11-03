package uz.uzkassa.smartpos.web.rest.fashion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.fashion.FashionIncomeCreateDto;
import uz.uzkassa.smartpos.dto.fashion.FashionIncomeDto;
import uz.uzkassa.smartpos.dto.fashion.FashionIncomeListDto;
import uz.uzkassa.smartpos.filter.FashionIncomeFilter;
import uz.uzkassa.smartpos.service.fashion.FashionIncomeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fashion/cabinet/income")
public class FashionCabinetIncomeResource {
    private final FashionIncomeService fashionIncomeService;

    @PostMapping
    public Long create(@RequestBody @Valid FashionIncomeCreateDto dto) {
        return fashionIncomeService.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody @Valid FashionIncomeCreateDto dto) {
        fashionIncomeService.update(id, dto);
    }

    @GetMapping("/{id}")
    public FashionIncomeDto getById(@PathVariable Long id) {
        return fashionIncomeService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        fashionIncomeService.delete(id);
    }

    @GetMapping
    public Page<FashionIncomeListDto> findAllByFilter(FashionIncomeFilter filter) {
        return fashionIncomeService.findAllByFilter(filter);
    }
}
