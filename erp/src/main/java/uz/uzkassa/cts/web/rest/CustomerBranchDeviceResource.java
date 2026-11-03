package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.dto.applicationfile.GenerateApplicationFileDTO;
import uz.uzkassa.cts.dto.customerbranchdevice.CustomerBranchDeviceRequestDTO;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceFreePeriodByCustomerIdDto;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceFreePeriodDto;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceListDto;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceTariffDto;
import uz.uzkassa.cts.dto.device.*;
import uz.uzkassa.cts.dto.sync.SyncCustomerDeviceListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CustomerBranchDeviceListFilter;
import uz.uzkassa.cts.service.CustomerBranchDeviceService;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.ValidationService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.util.List;
import java.util.Objects;

import static uz.uzkassa.cts.constants.MessageConstants.EXPORT_FILE_FAILED;

/**
 * REST controller for managing {@link CustomerBranchDevice}.
 */
@RestController
@RequestMapping("/api/customer/branch/devices")
@Tag(name = "Customer Branch Devices")
@Slf4j
@Deprecated
public class CustomerBranchDeviceResource {

    private final CustomerBranchDeviceService customerBranchDeviceService;
    private final FileUploadService fileUploadService;
    private final ValidationService validationService;

    public CustomerBranchDeviceResource(CustomerBranchDeviceService customerBranchDeviceService,
                                        FileUploadService fileUploadService, ValidationService validationService) {
        this.customerBranchDeviceService = customerBranchDeviceService;
        this.fileUploadService = fileUploadService;
        this.validationService = validationService;
    }

    @Operation(summary = "Add")
    @PostMapping
    public ResponseEntity<Void> add(@Valid @RequestBody CustomerBranchDeviceDTO customerBranchDeviceDTO) {
        log.debug("REST request to add device to Customer Branch: {}", customerBranchDeviceDTO.getCustomerBranchId());
        customerBranchDeviceService.add(customerBranchDeviceDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "report list")
    @GetMapping("/get-list")
    public ResponseEntity<Page<CustomerBranchDeviceListDto>> getList(CustomerBranchDeviceListFilter filter) {
        log.debug("REST request to get customer branch device list: {}", filter);
        return ResponseEntity.ok(customerBranchDeviceService.getList(filter));
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<CustomerBranchDeviceListDTO>> list(BaseFilter filter) {
        log.debug("REST request to get customer branch device list: {}", filter);
        return ResponseEntity.ok(customerBranchDeviceService.list(filter));
    }

    @Operation(summary = "List By Customer Branch")
    @GetMapping("/by-customer-branch")
    public ResponseEntity<Page<BaseDeviceFiscalModuleDTO>> listByCustomerBranch(BaseFilter filter) {
        log.debug("REST request to get customer branch device list: {}", filter);
        return ResponseEntity.ok(customerBranchDeviceService.listByCustomerBranch(filter));
    }

    @Operation(summary = "Look Up")
    @GetMapping("/lookup")
    public ResponseEntity<Page<SerialNumberDTO>> lookUp(BaseFilter filter) {
        log.debug("REST request to get customer branch device lookup: {}", filter);
        return ResponseEntity.ok(customerBranchDeviceService.lookUp(filter));
    }

    @Operation(summary = "Items")
    @GetMapping("/items")
    public ResponseEntity<List<SerialNumberDTO>> getItems(BaseFilter filter) {
        log.debug("REST request to get customer branch device items: {}", filter);
        return ResponseEntity.ok(customerBranchDeviceService.getItems(filter));
    }

    @Operation(summary = "Free Devices")
    @GetMapping("/items/{customerId}")
    public ResponseEntity<List<CustomerDeviceListDTO>> getFreeDevices(@PathVariable Long customerId) {
        log.debug("REST request to get customer device items:{}", customerId);
        return ResponseEntity.ok(customerBranchDeviceService.getFreeDevices(customerId));
    }

    @Operation(summary = "Download Application File")
    @GetMapping("/download/application/file")
    public ResponseEntity<Resource> downloadApplicationFile(@Valid GenerateApplicationFileDTO generateApplicationFileDTO) {
        log.debug("REST request to generate application file: {}", generateApplicationFileDTO);

        try {
            Resource resource = customerBranchDeviceService.downloadApplicationFile(generateApplicationFileDTO);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=applicationFile.pdf")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_PDF)
                    .cacheControl(CacheControl.noCache())
                    .body(resource);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw validationService.badRequestExceptionThrow(EXPORT_FILE_FAILED, e.getMessage()).get();
        }
    }

    @Operation(summary = "Delete Application File")
    @DeleteMapping("/application/file/{applicationFileId}")
    public ResponseEntity<Void> deleteApplicationFile(@PathVariable(value = "applicationFileId") Long applicationFileId) {
        log.debug("REST request to delete application file: {}", applicationFileId);
        fileUploadService.deleteById(applicationFileId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Count List to Sync")
    @GetMapping("/sync/count")
    public ResponseEntity<Long> getListCountToSync() {
        log.debug("REST request to get all customer branch device count");
        return ResponseEntity.ok(customerBranchDeviceService.getListCountToSync());
    }

    @Operation(summary = "Get List to Sync")
    @GetMapping("/sync/list")
    public ResponseEntity<Page<SyncCustomerDeviceListDTO>> getListToSync(BaseFilter filter) {
        log.debug("REST request to get all customer branch devices : {}", filter);
        return ResponseEntity.ok(customerBranchDeviceService.getListToSync(filter));
    }

    @Operation(summary = "Transfer")
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody CustomerBranchDeviceTransferDTO customerBranchDeviceTransferDTO) {
        customerBranchDeviceService.transferDeviceToOtherBranch(customerBranchDeviceTransferDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get device with tariff")
    @GetMapping("/customer/{customerId}/device/{deviceId}")
    public ResponseEntity<CustomerBranchDeviceTariffDto> getDevice(@PathVariable Long customerId, @PathVariable Long deviceId) {
        return ResponseEntity.ok(customerBranchDeviceService.getCustomerDeviceById(customerId, deviceId));
    }

    @Operation(summary = "Get device tariff")
    @GetMapping("/tariff/{customerId}/{serialNumber}")
    public ResponseEntity<String> getTariff(@PathVariable Long customerId, @PathVariable String serialNumber) {
        return ResponseEntity.ok(customerBranchDeviceService.getTariffByCustomerAndDevice(customerId, serialNumber));
    }

    @Operation(summary = "Get device with tariff")
    @PostMapping("/set-free-period")
    public ResponseEntity<CustomerBranchDeviceListDto> getDevice(@RequestBody @Valid CustomerBranchDeviceFreePeriodDto dto) {
        return ResponseEntity.ok(customerBranchDeviceService.setFreePeriodDate(dto));
    }

    @Operation(summary = "Get device with tariff")
    @PostMapping("/set-free-period-by-customer-id")
    public ResponseEntity<CustomerBranchDeviceListDto> getDevice(@RequestBody @Valid CustomerBranchDeviceFreePeriodByCustomerIdDto dto) {
        customerBranchDeviceService.setFreePeriodDateByCustomerId(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change payment status enum")
    @PutMapping("/change-payment-status")
    public ResponseEntity<Boolean> getDevice(@RequestBody @Valid CustomerBranchDeviceRequestDTO dto) {
        customerBranchDeviceService.changePaymentStatus(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Import excel: set the payment type status (HUMO/UZ_CARD)")
    @PostMapping("/import-payment-status/{status}/{typeTin}")
    public ResponseEntity<Void> importExcel(@RequestParam("file") MultipartFile file, @PathVariable String status, @PathVariable String typeTin) {
        if (Objects.isNull(file) || file.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Загружаемый файл не должен быть нулевым!");

        if (StringUtils.isEmpty(status))
            throw new BadRequestException("Статус платежа не должен быть пустым!");

        customerBranchDeviceService.importExcel(file, status, typeTin);
        return ResponseEntity.ok().build();
    }
}
