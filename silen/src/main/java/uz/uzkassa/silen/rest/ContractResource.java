package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.ContractDTO;
import uz.uzkassa.silen.dto.EnumDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.ContractFilter;
import uz.uzkassa.silen.enumeration.Status;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.service.ContractService;

import java.util.Arrays;
import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Contract}.
 */
@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContractResource {
    ContractService contractService;

    @PostMapping
    public ResponseEntity<ContractDTO> create(@RequestBody @Valid ContractDTO contractDTO) {
        contractService.save(contractDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<ContractDTO> update(@RequestBody @Valid ContractDTO contractDTO) {
        if (contractDTO.getId() == null) {
            throw new BadRequestException("ID required!");
        }
        contractService.save(contractDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/{status}")
    public ResponseEntity<ContractDTO> changeStatus(@PathVariable("id") String id, @PathVariable("status") Status status) {
        contractService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ContractDTO>> getAll(@ModelAttribute ContractFilter filter) {
        if (filter.getCustomerId() == null) {
            throw new BadRequestException("Клиент не найден");
        }
        return ResponseEntity.ok(contractService.findAll(filter));
    }

    @GetMapping("/items")
    public ResponseEntity<List<SelectItem>> getItems(@ModelAttribute ContractFilter filter) {
        return ResponseEntity.ok(contractService.getItems(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractDTO> get(@PathVariable String id) {
        return ResponseEntity.ok(contractService.findOne(id));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<EnumDTO>> statusList() {
        return ResponseEntity.ok(Arrays.asList(Status.DRAFT.toDto(), Status.ACCEPTED.toDto()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ContractDTO> delete(@PathVariable String id) {
        contractService.delete(id);
        return ResponseEntity.ok().build();
    }
}
