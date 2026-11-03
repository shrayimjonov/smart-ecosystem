package uz.uzkassa.horeca.web.rest.cabinet;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.socialaddress.SocialAddressDTO;
import uz.uzkassa.horeca.dto.socialaddress.SocialAddressDetailDTO;
import uz.uzkassa.horeca.enums.SocialAddressType;
import uz.uzkassa.horeca.service.SocialAddressService;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping(ApiConstants.cabinetSocialAddressRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CabinetSocialAddressResources {

    SocialAddressService socialAddressService;

    @PostMapping
    public ResponseEntity<SocialAddressDetailDTO> create(@RequestBody SocialAddressDTO socialAddressDTO) {
        socialAddressService.create(socialAddressDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<SocialAddressDetailDTO> update(@PathVariable Long id, @RequestBody SocialAddressDTO socialAddressDTO) {
        socialAddressService.update(id, socialAddressDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<SocialAddressDetailDTO>> getList() {
        return ResponseEntity.ok(socialAddressService.getList());
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<SocialAddressType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(SocialAddressType.class));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<Void> getList(@PathVariable Long id) {
        socialAddressService.delete(id);
        return ResponseEntity.ok().build();
    }
}
