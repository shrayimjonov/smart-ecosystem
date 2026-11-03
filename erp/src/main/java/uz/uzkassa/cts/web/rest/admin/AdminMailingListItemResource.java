package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.maillinglist.MailingListItemListDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.MailingListItemStatus;
import uz.uzkassa.cts.filter.MailingListItemFilter;
import uz.uzkassa.cts.service.MailingListItemService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminMailingListItemRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminMailingListItemResource {

    MailingListItemService mailingListItemService;

    @PostMapping(value =ApiConstants.uploadFile, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(@PathVariable Long id, @RequestParam MultipartFile file, @RequestParam EntityTypeEnum entityType) {
        mailingListItemService.upload(file, entityType, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<MailingListItemStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(MailingListItemStatus.class));
    }

    @GetMapping("/entity-types")
    public ResponseEntity<EnumSet<EntityTypeEnum>> getEntityTypes() {
        return ResponseEntity.ok(EnumSet.of(EntityTypeEnum.LEAD, EntityTypeEnum.CUSTOMER));
    }

    @GetMapping
    public ResponseEntity<Page<MailingListItemListDTO>> getList(@ParameterObject MailingListItemFilter filter) {
        return ResponseEntity.ok(mailingListItemService.getList(filter));
    }
}
