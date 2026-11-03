package uz.uzkassa.smartpos.web.rest.fashion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionTransferCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionTransferUpdateStatusDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.TransferDetailFashionDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.TransferFashionDto;
import uz.uzkassa.smartpos.service.FashionTransferService;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.OWNER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fashion/cabinet/transfer")
public class FashionTransferResource {
    private final FashionTransferService fashionTransferService;

    @PostMapping("/request")
    public Long requestTransfer(@RequestBody @Valid FashionTransferCreateDto dto) {
        return fashionTransferService.create(dto);
    }

    @PostMapping("/send")
    @Secured(OWNER)
    public Long sendTransfer(@RequestBody @Valid FashionTransferCreateDto dto) {
        return fashionTransferService.send(dto);
    }

    @PutMapping("/{id}")
    public void updateStatus(@PathVariable Long id, @RequestBody FashionTransferUpdateStatusDto dto) {
        fashionTransferService.updateStatus(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        fashionTransferService.delete(id);
    }

    @GetMapping("/{id}")
    public TransferFashionDto<TransferDetailFashionDto> getById(@PathVariable Long id) {
        return fashionTransferService.getById(id);
    }
}
