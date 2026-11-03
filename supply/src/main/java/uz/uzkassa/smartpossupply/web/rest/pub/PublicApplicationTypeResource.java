package uz.uzkassa.smartpossupply.web.rest.pub;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.enums.ApplicationType;

import java.util.EnumSet;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 15:04
 */
@RestController
@RequestMapping(ApiConstants.publicApplicationTypeRootApi)
public class PublicApplicationTypeResource {

    @GetMapping
    public ResponseEntity<EnumSet<ApplicationType>> getApplicationTypes() {
        return ResponseEntity.ok(EnumSet.allOf(ApplicationType.class));
    }
}
