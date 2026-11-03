package uz.uzkassa.smartpossupply.web.rest.pub;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.enums.*;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.publicProductRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicProductResource {

    @Operation(summary = "VATs")
    @GetMapping(ApiConstants.vats)
    public ResponseEntity<EnumSet<Vat>> getVats() {
        return ResponseEntity.ok(EnumSet.of(Vat.WITHOUT_VAT, Vat.VAT_0, Vat.VAT_6, Vat.VAT_12));
    }

    @Operation(summary = ApiConstants.Groups)
    @GetMapping(ApiConstants.groups)
    public ResponseEntity<EnumSet<ProductGroup>> getGroups() {
        return ResponseEntity.ok(EnumSet.allOf(ProductGroup.class));
    }

    @Operation(summary = ApiConstants.Types)
    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<ProductType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(ProductType.class));
    }

    @Operation(summary = "Product origins")
    @GetMapping("/origins")
    public ResponseEntity<EnumSet<ProductOrigin>> getOrigins() {
        return ResponseEntity.ok(EnumSet.allOf(ProductOrigin.class));
    }

    @Operation(summary = "Product origins")
    @GetMapping("/aic/additional-units")
    public ResponseEntity<EnumSet<AicAdditionalUnit>> getAdditionalUnits() {
        return ResponseEntity.ok(EnumSet.allOf(AicAdditionalUnit.class));
    }
}
