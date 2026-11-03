package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.mailinglist.MailingListItemListDTO;
import uz.uzkassa.smartpossupply.enums.MailingListItemStatus;
import uz.uzkassa.smartpossupply.filters.MailingListItemFilter;
import uz.uzkassa.smartpossupply.service.MailingListItemService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetMailingListItemRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetMailingListItemResource {

    MailingListItemService mailingListItemService;

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<MailingListItemStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(MailingListItemStatus.class));
    }

    @GetMapping
    public ResponseEntity<Page<MailingListItemListDTO>> getList(@ParameterObject MailingListItemFilter filter) {
        return ResponseEntity.ok(mailingListItemService.getList(filter));
    }
}