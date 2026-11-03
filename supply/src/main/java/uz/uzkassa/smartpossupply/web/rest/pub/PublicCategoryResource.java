package uz.uzkassa.smartpossupply.web.rest.pub;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.publicCategoryRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicCategoryResource {

    @GetMapping("/environment-types")
    public ResponseEntity<EnumSet<EnvironmentType>> getEnvironmentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(EnvironmentType.class));
    }
}
