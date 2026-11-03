package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.dto.marking.PartyDTO;
import uz.uzkassa.silen.dto.marking.PartyMergeDTO;
import uz.uzkassa.silen.dto.marking.PartyUtilisationDTO;
import uz.uzkassa.silen.enumeration.PartyStatus;
import uz.uzkassa.silen.enumeration.UsageType;
import uz.uzkassa.silen.service.PartyService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Party}.
 */
@RestController
@RequestMapping("/api/orders/party")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartyResource {
    PartyService partyService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody PartyDTO partyDTO) {
        partyService.save(partyDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/upload/{orderProductId}/{quantity}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadParty(@PathVariable("orderProductId") String orderProductId,
                                            @PathVariable("quantity") int quantity,
                                            @RequestParam(name = "file", required = false) MultipartFile file) {
        partyService.uploadParty(orderProductId, quantity, file);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartyStatus> apply(@PathVariable String id, @RequestBody @Valid PartyUtilisationDTO partyUtilisationDTO) {
        return ResponseEntity.ok(partyService.apply(id, partyUtilisationDTO));
    }

    @PutMapping("/merge")
    public ResponseEntity<Void> merge(@RequestBody @Valid PartyMergeDTO dto) {
        partyService.merge(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<PartyDTO>> getAll(@ModelAttribute MongoFilter filter) {
        return ResponseEntity.ok(partyService.findAll(filter));
    }


    @GetMapping("/items")
    public ResponseEntity<List<SelectItem>> getItems(@ModelAttribute MongoFilter filter) {
        return ResponseEntity.ok(partyService.getItems(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartyDTO> getItems(@PathVariable String id) {
        return ResponseEntity.ok(partyService.findOne(id));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<SelectItem>> getStatuses() {
        return ResponseEntity.ok(Arrays.asList(PartyStatus.EMITTED.toSelectItem(), PartyStatus.APPLIED.toSelectItem()));
    }

    @GetMapping("/usage-types")
    public ResponseEntity<List<SelectItem>> getUsageTypes() {
        return ResponseEntity.ok(Arrays.stream(UsageType.values()).map(UsageType::toSelectItem).collect(Collectors.toList()));
    }

    @GetMapping("/retry/{id}")
    public ResponseEntity<Void> retry(@PathVariable final String id) {
        partyService.retryPartyMarks(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Long>> checkParties(@RequestParam List<String> partyIds) {
        return ResponseEntity.ok(partyService.checkParties(partyIds));
    }
}
