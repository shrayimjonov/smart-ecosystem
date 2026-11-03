package uz.uzkassa.smartpossupply.web.rest.mobile;

import com.opencsv.exceptions.CsvValidationException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aic.AicDTO;
import uz.uzkassa.smartpossupply.dto.aic.AicDetailsDTO;
import uz.uzkassa.smartpossupply.dto.aic.AicListDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.AicDocumentRequest;
import uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.privateinfo.CisPrivateInfoResponse;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.smartpossupply.enums.AicStatus;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.integration.EimzoClient;
import uz.uzkassa.smartpossupply.service.AicService;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.mobileAicRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileAicResource {
    AicService aicService;
    EimzoClient eimzoClient;

    @Operation(summary = ApiConstants.Create, operationId = "createAic")
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody AicDTO aicDTO) {
        return ResponseEntity.ok(aicService.create(aicDTO));
    }

    @Operation(summary = "Confirmation", operationId = "confirmationAic")
    @PostMapping("/confirm")
    public ResponseEntity<Long> confirmation(@RequestBody @Valid final AicDocumentRequest request) {
        final Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(request.getSignature());
        if (pkcs7Dto.isSuccess()) {
            request.setSignature(pkcs7Dto.getPkcs7b64());
            return ResponseEntity.ok(aicService.confirmation(request));
        } else {
            throw new BadRequestException("Not verified, reason: " + pkcs7Dto.getMessage());
        }
    }

    @Operation(summary = "DisAggregation", operationId = "disAggregationAic")
    @PostMapping("/disaggregation")
    public ResponseEntity<Long> disAggregation(@RequestBody @Valid AicDocumentRequest request) {
        final Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(request.getSignature());
        if (pkcs7Dto.isSuccess()) {
            request.setSignature(pkcs7Dto.getPkcs7b64());
            return ResponseEntity.ok(aicService.disAggregation(request));
        } else {
            throw new BadRequestException("Not verified, reason: " + pkcs7Dto.getMessage());
        }
    }

    @Operation(summary = ApiConstants.List, operationId = "listAic")
    @GetMapping
    public ResponseEntity<Page<AicListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(aicService.getList(filter));
    }

    @Operation(summary = ApiConstants.Get, operationId = "getAic")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<AicDetailsDTO> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(aicService.get(id));
    }

    @Operation(summary = ApiConstants.Statuses, operationId = "getAicStatuses")
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<AicStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(AicStatus.class));
    }

    @Operation(summary = ApiConstants.LookUp, operationId = "getAicLookup")
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(aicService.lookUp(filter));
    }

    @Operation(summary = "Read data from file", operationId = "readAicFromFile")
    @PostMapping(value = "/file/{type}/product/{productType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<CisInfoResponse>> readFromFile(@PathVariable("type") FileType type, @PathVariable("productType") ProductType productType, @RequestParam("file") MultipartFile file) throws IOException, CsvValidationException {
        return ResponseEntity.ok(aicService.readFromFile(type, productType, file));
    }

    @Operation(summary = "Check code from aic codes", operationId = "checkAicCodes")
    @PostMapping(value = ApiConstants.check)
    public ResponseEntity<List<CisInfoResponse>> checkFromCodes(@RequestBody @Valid BatchValidationDTO request) {
        return ResponseEntity.ok(aicService.check(request));
    }

    @Operation(summary = "Get aic detail for stock in", operationId = "getAicDetailForStockIn")
    @GetMapping(value = "/by-aic/{aic}/{branchId}")
    public ResponseEntity<CisPrivateInfoResponse> getDetailForStockIn(@PathVariable String aic, @PathVariable Long branchId) {
        return ResponseEntity.ok(aicService.getDetailOrCreateForStockIn(aic, branchId));
    }

    @Operation(summary = "Get file types", operationId = "getFileTypes")
    @GetMapping(value = "/file-types")
    public ResponseEntity<EnumSet<FileType>> getFileTypes() {
        return ResponseEntity.ok(EnumSet.allOf(FileType.class));
    }
}
