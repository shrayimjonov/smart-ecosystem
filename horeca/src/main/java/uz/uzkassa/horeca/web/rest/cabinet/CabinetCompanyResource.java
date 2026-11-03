package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.company.CompanyDTO;
import uz.uzkassa.horeca.dto.company.cabinet.CompanyDetailCabinetDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.sync.CompanySyncDTO;
import uz.uzkassa.horeca.service.CompanyService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCompanyRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCompanyResource {

    CompanyService companyService;

    @PutMapping
    public ResponseEntity<Long> update(@RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyService.update(null,companyDTO));
    }

    @GetMapping
    public ResponseEntity<CompanyDetailCabinetDTO> get() {
        return ResponseEntity.ok(companyService.getForCabinet());
    }

    @PostMapping(value = "/upload/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadLogo(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(companyService.uploadLogo(file));
    }

    @DeleteMapping("/logo")
    public ResponseEntity<Void> removeLogo() {
        companyService.removeLogo();
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/upload/background-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadBackgroundPhoto(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(companyService.uploadBackgroundPhoto(file));
    }

    @DeleteMapping("/background-photo")
    public ResponseEntity<Void> removeBackgroundPhoto() {
        companyService.removeBackgroundPhoto();
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.sync + "/{id}")
    public ResponseEntity<Long> apaySync(@PathVariable Long id) {
        companyService.apaySync(id);
        return ResponseEntity.ok().build();
    }
}
