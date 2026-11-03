package uz.uzkassa.horeca.web.rest.pub;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.service.RegionService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.publicRegionRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicRegionResource {

    RegionService regionService;

    @GetMapping(ApiConstants.items)
    public ResponseEntity<List<CommonDTO>> getItems() {
        return ResponseEntity.ok(regionService.getItems());
    }
}
