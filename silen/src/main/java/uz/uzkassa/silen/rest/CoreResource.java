package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.FacturaHostDTO;
import uz.uzkassa.silen.dto.SettingsDTO;
import uz.uzkassa.silen.dto.invoice.*;
import uz.uzkassa.silen.dto.vm.SignVM;
import uz.uzkassa.silen.integration.FacturaClient;
import uz.uzkassa.silen.security.SecurityUtils;
import uz.uzkassa.silen.service.CoreService;
import uz.uzkassa.silen.service.ProductService;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CoreResource {
    private final FacturaClient facturaClient;
    private final ProductService productService;
    private final CoreService coreService;

    @GetMapping("/guid")
    public ResponseEntity<ResponseDTO> getGuid() {
        return ResponseEntity.ok(coreService.getGuid());
    }

    @GetMapping("/company/catalog")
    public ResponseEntity<List<MxikDTO>> getCompanyCatalog(@RequestParam(required = false) String tin,
                                                           @RequestParam(required = false) String search) {
        return ResponseEntity.ok(coreService.getCompanyCatalog(tin, search));
    }

    @GetMapping("/packages")
    public ResponseEntity<List<PackageDTO>> getPackages(@RequestParam String mxikCode) {
        return ResponseEntity.ok(coreService.getPackages(mxikCode));
    }

    @GetMapping("/lgota")
    public ResponseEntity<List<LgotaDTO>> getLgotas(@RequestParam(required = false) String mxikCode,
                                                    @RequestParam(required = false) String tin) {
        if (tin == null) {
            tin = SecurityUtils.getCurrentOrganizationTin();
        }
        LgotaResponseDTO lgotaResponseDTO = facturaClient.getLgotaByTin(tin);

        List<LgotaDTO> tinLgotas = (lgotaResponseDTO != null) ? lgotaResponseDTO.getRows() : Collections.emptyList();

        if (StringUtils.isNotEmpty(mxikCode)) {
            List<LgotaDTO> productLgotas = facturaClient.getLgota(mxikCode);
            tinLgotas.addAll(productLgotas);
        }
        return ResponseEntity.ok(tinLgotas);
    }

    @GetMapping("/providers")
    public ResponseEntity<ProviderResultDTO> getProviders() {
        return ResponseEntity.ok(coreService.getProviders());
    }

    @PostMapping("/providers")
    public ResponseEntity<Void> saveProviders(@RequestBody @Valid SignVM signVM) {
        coreService.saveProviders(signVM);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/update-product")
    public ResponseEntity<Void> updateProduct() {
        productService.updateCode();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/regions")
    public ResponseEntity<List<RegionDTO>> getRegions() {
        List<RegionDTO> regionDTOS = coreService.getRegions();
        return ResponseEntity.ok(regionDTOS);
    }

    @GetMapping("/sync/regions")
    public ResponseEntity<Void> syncRegions() {
        coreService.syncRegions();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/districts/{regionId}")
    public ResponseEntity<List<DistrictDTO>> getDistricts(@PathVariable("regionId") Long regionId) {
        List<DistrictDTO> districtDTOS = coreService.getDistricts(regionId);
        return ResponseEntity.ok(districtDTOS);
    }

    @GetMapping("/sync/districts")
    public ResponseEntity<List<DistrictDTO>> syncDistricts(@RequestParam(value = "regionId", required = false) Long regionId) {
        if (regionId != null) coreService.syncDistricts(regionId);
        else coreService.syncDistricts();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/villages/{soato}")
    public ResponseEntity<VillageDTO[]> getVillages(@PathVariable("soato") String soato) {
        return ResponseEntity.ok(coreService.getVillages(soato));
    }

    @GetMapping("/banks")
    public ResponseEntity<List<BankDTO>> getBanks() {
        List<BankDTO> bankDTOS = coreService.getBanks();
        return ResponseEntity.ok(bankDTOS);
    }

    @GetMapping("/measures")
    public ResponseEntity<List<MeasureDTO>> getMeasures() {
        List<MeasureDTO> measureDTOS = coreService.getMeasures();
        return ResponseEntity.ok(measureDTOS);
    }

    @GetMapping("/sync/packages")
    public ResponseEntity<String> syncPackages() {
        coreService.syncPackages();
        return ResponseEntity.ok("Синхронизация началась, и вы будете уведомлены после завершения");

    }

    @PutMapping("/factura-host/{id}")
    public ResponseEntity<Long> setActiveHostForFactura(@PathVariable("id") Long id) {
        return ResponseEntity.ok(coreService.setActiveHostForFactura(id));
    }

    @GetMapping("/factura-host")
    public ResponseEntity<List<FacturaHostDTO>> setActiveHostForFactura() {
        return ResponseEntity.ok(coreService.getAllHostForFactura());
    }

    @PutMapping("/settings/app-version")
    public ResponseEntity<Void> updateAppVersion(@RequestBody final SettingsDTO settingsDTO) {
        coreService.updateAppVersion(settingsDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/settings/app-version")
    public ResponseEntity<String> getAppVersion() {
        return ResponseEntity.ok(coreService.getAppVersion());
    }
}
