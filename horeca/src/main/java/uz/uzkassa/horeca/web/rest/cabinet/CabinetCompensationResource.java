package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.compensation.CompensationDTO;
import uz.uzkassa.horeca.dto.compensation.CompensationDetailCabinetDTO;
import uz.uzkassa.horeca.dto.compensation.CompensationListCabinetDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.CompensationService;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.cabinetCompensationRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class CabinetCompensationResource {

    CompensationService compensationService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CompensationDTO compensationDTO) {
        return ResponseEntity.ok(compensationService.create(compensationDTO));
    }

    @PostMapping(ApiConstants.batch)
    public ResponseEntity<Void> createBatch(@RequestBody List<CompensationDTO> compensationDTOList) {
        compensationService.createBatch(compensationDTOList);
        return ResponseEntity.ok().build();
    }

    @PutMapping(ApiConstants.batch)
    public ResponseEntity<Void> updateBatch(@RequestBody List<CompensationDTO> compensationDTOList) {
        compensationService.updateBatch(compensationDTOList);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CompensationDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(compensationService.getForCabinet(id));
    }

    @GetMapping
    public ResponseEntity<Page<CompensationListCabinetDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(compensationService.getListForCabinet(filter));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CompensationDTO compensationDTO) {
        return ResponseEntity.ok(compensationService.update(id, compensationDTO));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        compensationService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{id}/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile photo) {
        return ResponseEntity.ok(compensationService.uploadPhoto(id, photo));
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Void> removePhoto(@PathVariable Long id) {
        compensationService.removePhoto(id);
        return ResponseEntity.ok().build();
    }
}
