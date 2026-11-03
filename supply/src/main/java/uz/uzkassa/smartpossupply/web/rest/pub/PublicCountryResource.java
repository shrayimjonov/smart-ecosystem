package uz.uzkassa.smartpossupply.web.rest.pub;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.Continent;
import uz.uzkassa.smartpossupply.service.CountryService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.publicCountryRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicCountryResource {

    CountryService countryService;

    @GetMapping(ApiConstants.items)
    public ResponseEntity<List<CommonDTO>> getItems(@RequestParam(value = "continent", required = false) String continent) {
        return ResponseEntity.ok(countryService.getItems(Continent.getByCode(continent)));
    }

    @GetMapping("/continents")
    public ResponseEntity<EnumSet<Continent>> getContinents() {
        return ResponseEntity.ok(EnumSet.allOf(Continent.class));
    }

}
