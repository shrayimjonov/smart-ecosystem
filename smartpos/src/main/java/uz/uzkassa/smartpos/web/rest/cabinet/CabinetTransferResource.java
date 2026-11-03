package uz.uzkassa.smartpos.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.base.PageDto;
import uz.uzkassa.smartpos.dto.filter.TransferFilter;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionTransferCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionTransferUpdateStatusDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.TransferListDto;
import uz.uzkassa.smartpos.service.TransferServiceV2;

@RequestMapping("/api/v1/cabinet/transfer")
@RestController
@RequiredArgsConstructor
public class CabinetTransferResource {
    private final TransferServiceV2 transferService;

    @GetMapping
    public PageDto<TransferListDto> findAllByFilter(@ParameterObject TransferFilter filter) {
        return transferService.findAllByFilter(filter);
    }

    @PostMapping
    public Long create(@RequestBody @Valid FashionTransferCreateDto dto) {
        return transferService.create(dto, false);
    }

    @PostMapping("/send")
    public Long send(@RequestBody @Valid FashionTransferCreateDto dto) {
        return transferService.create(dto, true);
    }

    @PutMapping("/{id}")
    public void updateStatus(@PathVariable Long id, @RequestBody FashionTransferUpdateStatusDto dto) {
        transferService.updateStatus(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        transferService.delete(id);
    }
}
