package uz.uzkassa.smartpos.web.rest.terminal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.warehouse.MarkingWarehouseCheckDto;
import uz.uzkassa.smartpos.service.warehouse.MarkingWarehouseService;

@RestController
@RequestMapping("/api/v1/terminals/markings")
@RequiredArgsConstructor
public class TerminalMarkingResource {
    private final MarkingWarehouseService markingWarehouseService;

    @PostMapping("/check")
    public void check(@RequestBody @Valid MarkingWarehouseCheckDto dto) {
        markingWarehouseService.check(dto);
    }
}
