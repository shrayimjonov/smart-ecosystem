package uz.uzkassa.smartpossupply.web.rest.cabinet;

import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.markorder.MarkOrderDTO;
import uz.uzkassa.smartpossupply.dto.markorder.MarkOrderProductListDTO;
import uz.uzkassa.smartpossupply.dto.markorder.MarkOrderProductLookUpDTO;
import uz.uzkassa.smartpossupply.enums.MarkOrderCreateMethodType;
import uz.uzkassa.smartpossupply.enums.MarkOrderRateType;
import uz.uzkassa.smartpossupply.enums.MarkOrderReleaseMethodType;
import uz.uzkassa.smartpossupply.enums.SerialNumberType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.service.MarkOrderProductService;
import uz.uzkassa.smartpossupply.service.MarkOrderService;
import uz.uzkassa.smartpossupply.service.ProductService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetMarkOrderRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetMarkOrderResource {

    MarkOrderService markOrderService;
    MarkOrderProductService markOrderProductService;
    ProductService productService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody MarkOrderDTO markOrderDTO) {
        return ResponseEntity.ok(markOrderService.create(markOrderDTO));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<MarkOrderProductListDTO>> getList(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(markOrderProductService.getList(filter));
    }

    @Operation(summary = "Products Look Up")
    @GetMapping("/products/lookup")
    public ResponseEntity<List<MarkOrderProductLookUpDTO>> getProductLookUp(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(productService.getLookUpToMarking(filter));
    }

    @GetMapping("/create-method-types")
    public ResponseEntity<EnumSet<MarkOrderCreateMethodType>> getAllCreateMethodTypes() {
        return ResponseEntity.ok(EnumSet.allOf(MarkOrderCreateMethodType.class));
    }

    @GetMapping("/release-method-types")
    public ResponseEntity<EnumSet<MarkOrderReleaseMethodType>> getAllReleaseMethodTypes() {
        return ResponseEntity.ok(markOrderService.getMarkOrderReleaseMethodType());
    }

    @GetMapping("/serial-number-types")
    public ResponseEntity<EnumSet<SerialNumberType>> getAllSerialNumberTypes() {
        return ResponseEntity.ok(EnumSet.allOf(SerialNumberType.class));
    }

    @GetMapping("/rate-types")
    public ResponseEntity<EnumSet<MarkOrderRateType>> getAllRateTypes() {
        return ResponseEntity.ok(EnumSet.allOf(MarkOrderRateType.class));
    }

    @Operation(summary = "Read data from file")
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Set<String>> readFromFile(@RequestParam("file") MultipartFile file) throws IOException, CsvValidationException {
        return ResponseEntity.ok(markOrderService.readFromFile(file));
    }
}
