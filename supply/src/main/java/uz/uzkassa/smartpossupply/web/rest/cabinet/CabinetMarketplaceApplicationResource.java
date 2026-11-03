package uz.uzkassa.smartpossupply.web.rest.cabinet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationBatchDTO;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationDTO;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationDetailDTO;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationListDTO;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationPhotoDTO;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationStatisticDTO;
import uz.uzkassa.smartpossupply.dto.marketplaceapplication.MarketplaceApplicationStatusUpdateDTO;
import uz.uzkassa.smartpossupply.enums.MarketplaceApplicationStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.MarketplaceApplicationService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetMarketplaceApplicationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetMarketplaceApplicationResource {

    MarketplaceApplicationService marketplaceApplicationService;
    ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody MarketplaceApplicationBatchDTO marketplaceApplicationBatchDTO) {
        marketplaceApplicationService.create(marketplaceApplicationBatchDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = ApiConstants.id, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> update(@PathVariable Long id,
                                       @RequestParam(name = "data", required = false) String marketplaceApplicationDTO,
                                       @RequestPart(name = "files", required = false) List<MultipartFile> photos) throws JsonProcessingException {
        return ResponseEntity.ok(marketplaceApplicationService.update(id, objectMapper.readValue(marketplaceApplicationDTO,MarketplaceApplicationDTO.class), photos));
    }

    @GetMapping
    public ResponseEntity<Page<MarketplaceApplicationListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(marketplaceApplicationService.getList(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<MarketplaceApplicationDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(marketplaceApplicationService.get(id));
    }

    @PutMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody MarketplaceApplicationStatusUpdateDTO marketplaceApplicationStatusUpdateDTO) {
        marketplaceApplicationService.updateStatus(id, marketplaceApplicationStatusUpdateDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<MarketplaceApplicationStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(MarketplaceApplicationStatus.class));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<MarketplaceApplicationStatisticDTO> getStatistics(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(marketplaceApplicationService.getStatistics(filter));
    }

    @PostMapping(ApiConstants.send)
    public ResponseEntity<Void> send(@RequestBody List<Long> ids) {
        marketplaceApplicationService.send(ids);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/remove/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        marketplaceApplicationService.remove(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/return/{id}")
    public ResponseEntity<Void> returnBack(@PathVariable Long id) {
        marketplaceApplicationService.returnBack(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/photos")
    public ResponseEntity<List<MarketplaceApplicationPhotoDTO>> getPhotos(@PathVariable Long id) {
        return ResponseEntity.ok(marketplaceApplicationService.getPhotos(id));
    }

}
