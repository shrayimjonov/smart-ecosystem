package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.maillinglist.MailingListDTO;
import uz.uzkassa.cts.dto.maillinglist.MailingListDetailDTO;
import uz.uzkassa.cts.dto.maillinglist.MailingListListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.MailingListService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminMailingListRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminMailingListResource {

    MailingListService mailingListService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody MailingListDTO mailingListDTO) {
        return ResponseEntity.ok(mailingListService.create(mailingListDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody MailingListDTO mailingListDTO) {
        return ResponseEntity.ok(mailingListService.update(id, mailingListDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<MailingListDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(mailingListService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<MailingListListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(mailingListService.getList(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(mailingListService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        mailingListService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
