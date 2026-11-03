package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.feedback.FeedbackDTO;
import uz.uzkassa.cts.dto.feedback.cabinet.FeedbackDetailCabinetDTO;
import uz.uzkassa.cts.dto.feedback.cabinet.FeedbackListCabinetDTO;
import uz.uzkassa.cts.filter.FeedbackFilter;
import uz.uzkassa.cts.service.FeedbackService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.cabinetFeedbackRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetFeedbackResource {

    FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody FeedbackDTO feedbackDTO) {
        return ResponseEntity.ok(feedbackService.create(feedbackDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody FeedbackDTO feedbackDTO) {
        return ResponseEntity.ok(feedbackService.update(id, feedbackDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<FeedbackDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getForCabinet(id));
    }

    @GetMapping
    public ResponseEntity<Page<FeedbackListCabinetDTO>> getList(@ParameterObject FeedbackFilter filter) {
        return ResponseEntity.ok(feedbackService.getListForCabinet(filter));
    }
}
