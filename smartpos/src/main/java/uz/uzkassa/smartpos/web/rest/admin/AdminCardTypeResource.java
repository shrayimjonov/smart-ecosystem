package uz.uzkassa.smartpos.web.rest.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.CardTypeDto;
import uz.uzkassa.smartpos.service.CardTypeService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/card-types")
public class AdminCardTypeResource {
    private final CardTypeService cardTypeService;

    @PostMapping
    public CardTypeDto create(@RequestBody @Valid CardTypeDto dto) {
        return cardTypeService.create(dto);
    }

    @PostMapping("/batch")
    public void batchCreate(@RequestBody List<String> prefixes) {
        cardTypeService.batchCreate(prefixes);
    }

    @DeleteMapping("/{prefix}")
    public void delete(@PathVariable String prefix) {
        cardTypeService.delete(prefix);
    }

    @GetMapping
    public List<CardTypeDto> findAll() {
        return cardTypeService.findAll();
    }
}
