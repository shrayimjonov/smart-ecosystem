package uz.uzkassa.horeca.web.rest.terminal;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.dto.eps.EpsServiceCrudDTO;
import uz.uzkassa.horeca.dto.eps.EpsServiceDTO;
import uz.uzkassa.horeca.dto.eps.EpsServiceLookupDTO;
import uz.uzkassa.horeca.dto.eps.TerminalEpsServicesLookupDto;
import uz.uzkassa.horeca.enums.terminal.EpsMethodType;
import uz.uzkassa.horeca.enums.terminal.EpsServiceStatusEnum;
import uz.uzkassa.horeca.service.EpsService;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/eps-services")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TerminalEpsServiceResource {

    EpsService epsService;

    @PostMapping
    public EpsServiceDTO create(@RequestBody @Valid EpsServiceCrudDTO dto) {
        return epsService.create(dto);
    }

    @PutMapping("/{id}")
    public EpsServiceDTO update(@PathVariable UUID id, @RequestBody @Valid EpsServiceCrudDTO dto) {
        return epsService.update(id, dto);
    }

    @GetMapping("/{id}")
    public EpsServiceDTO findById(@PathVariable UUID id) {
        return epsService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        epsService.delete(id);
    }

    @GetMapping
    public List<EpsServiceDTO> findAll() {
        return epsService.getList(false);
    }

    @GetMapping("/lookup")
    public List<TerminalEpsServicesLookupDto> lookup(@RequestParam(required = false) Long branchId) {
        return epsService.lookup(branchId);
    }

    @GetMapping("/sync")
    public void sync() {
        epsService.sync();
    }

    @GetMapping("/statuses")
    public EpsServiceStatusEnum[] statuses() {
        return EpsServiceStatusEnum.values();
    }

    @GetMapping("/methods")
    public EpsMethodType[] methodTypes() {
        return EpsMethodType.values();
    }
}
