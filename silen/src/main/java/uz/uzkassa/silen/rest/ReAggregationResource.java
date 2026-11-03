package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.marking.AggregationDTO;
import uz.uzkassa.silen.dto.mongo.MarkDTO;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.service.AggregationService;
import uz.uzkassa.silen.service.MarkService;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Aggregation}.
 */
@RestController
@RequestMapping("/api/re-aggregation")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReAggregationResource {
    MarkService markService;
    AggregationService aggregationService;

//    @PostMapping("/{id}")
//    public ResponseEntity<AggregationStatus> send(@PathVariable String id, @RequestParam(required = false) String organizationId) {
//        if (StringUtils.isNotEmpty(organizationId)) {
//            final EnumSet<AggregationStatus> statuses = EnumSet.of(AggregationStatus.CREATED, AggregationStatus.UPDATED, AggregationStatus.ERROR, AggregationStatus.IN_PROGRESS);
//            final LocalDateTime localDateTime = LocalDateTime.now().minusHours(1);
//            aggregationService.checkAndResend(organizationId, statuses, localDateTime);
//            return ResponseEntity.ok(AggregationStatus.CREATED);
//        } else {
//            return ResponseEntity.ok(aggregationService.checkAndUtiliseById(id));
//        }
//    }

    @GetMapping("/{id}")
    public ResponseEntity<AggregationDTO> getAggregation(@PathVariable String id) {
        AggregationDTO result = aggregationService.findAggregationWithMarks(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/check")
    public ResponseEntity<List<MarkDTO>> check(@RequestBody @Valid List<MarkDTO> markDTOs) {
        boolean hasError = false;
        List<MarkDTO> result = new ArrayList<>();
        for (MarkDTO markDTO : markDTOs) {
            try {
                result.add(markService.checkMark(markDTO));
            } catch (BadRequestException e) {
                hasError = true;
                result.add(markDTO);
            }
        }
        if (hasError) {
            throw new BadRequestException("Не прошла проверка у 1 или несколько кодов");
        }
        return ResponseEntity.ok(result);
    }
}
