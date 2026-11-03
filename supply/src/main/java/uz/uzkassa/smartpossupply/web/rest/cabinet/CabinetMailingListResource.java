package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.mailinglist.MailingListDTO;
import uz.uzkassa.smartpossupply.dto.mailinglist.MailingListDetailDTO;
import uz.uzkassa.smartpossupply.dto.mailinglist.MailingListListDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.MailingListService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetMailingListRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetMailingListResource {

    MailingListService mailingListService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody MailingListDTO mailingListDTO) {
        return ResponseEntity.ok(mailingListService.create(mailingListDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<MailingListDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(mailingListService.get(id));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody MailingListDTO mailingListDTO) {
        return ResponseEntity.ok(mailingListService.update(id, mailingListDTO));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<CommonStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

    @GetMapping
    public ResponseEntity<Page<MailingListListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(mailingListService.getList(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return ResponseEntity.ok(mailingListService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        mailingListService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mailingListService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
