package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.address.AddressDTO;
import uz.uzkassa.cts.dto.address.AddressListDTO;
import uz.uzkassa.cts.enums.AddressType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.service.AddressService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminAddressRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminAddressResource {

    AddressService addressService;

    @PostMapping("/{entityType}/{entityId}")
    public ResponseEntity<Long> create(@PathVariable EntityTypeEnum entityType, @PathVariable Long entityId, @RequestBody AddressDTO addressDTO) {
        return ResponseEntity.ok(addressService.create(entityType, entityId, addressDTO));
    }

    @PutMapping("/{entityType}/{entityId}")
    public ResponseEntity<Long> update(@PathVariable EntityTypeEnum entityType, @PathVariable Long entityId, @RequestBody AddressDTO addressDTO) {
        return ResponseEntity.ok(addressService.update(entityType, entityId, addressDTO));
    }

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<List<AddressListDTO>> getList(@PathVariable EntityTypeEnum entityType, @PathVariable Long entityId) {
        return ResponseEntity.ok(addressService.getList(entityType, entityId));
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<AddressType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(AddressType.class));
    }

    @PostMapping(ApiConstants.batch + "/{entityType}/{entityId}")
    public ResponseEntity<Void> createBatch(@PathVariable EntityTypeEnum entityType, @PathVariable Long entityId, @RequestBody List<AddressDTO> addressDTO) {
        addressService.createBatch(entityType, entityId, addressDTO);
        return ResponseEntity.ok().build();
    }
}
