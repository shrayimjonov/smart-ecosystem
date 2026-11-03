package uz.uzkassa.silen.rest;


import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.EnumDTO;
import uz.uzkassa.silen.dto.PrintAggregationDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.filter.PartyAggregationFilter;
import uz.uzkassa.silen.dto.partyaggregation.PartyAggregationDTO;
import uz.uzkassa.silen.enumeration.PartyAggregationStatus;
import uz.uzkassa.silen.service.PartyAggregationService;

import java.util.List;

@RestController
@RequestMapping("/api/party/aggregation")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartyAggregationResource {
    PartyAggregationService partyAggregationService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody PartyAggregationDTO partyAggregationDTO) {
        return ResponseEntity.ok(partyAggregationService.create(partyAggregationDTO));
    }

    @GetMapping
    public ResponseEntity<Page<PartyAggregationDTO>> findAllByFilter(@ModelAttribute PartyAggregationFilter filter) {
        return ResponseEntity.ok(partyAggregationService.findAllByFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartyAggregationDTO> getOne(@PathVariable String id) {
        return ResponseEntity.ok(partyAggregationService.getOne(id));
    }

    @GetMapping("/items")
    public ResponseEntity<List<SelectItem>> getItems(@ModelAttribute PartyAggregationFilter filter) {
        return ResponseEntity.ok(partyAggregationService.getItems(filter));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<EnumDTO>> getStatuses() {
        return ResponseEntity.ok(PartyAggregationStatus.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable String id, @RequestBody @Valid PartyAggregationDTO partyAggregationDTO) {
        return ResponseEntity.ok(partyAggregationService.update(id, partyAggregationDTO));
    }

    @PutMapping("/close/{id}")
    public ResponseEntity<Void> aggregate(@PathVariable String id) {
        partyAggregationService.aggregate(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/codes/{id}")
    public ResponseEntity<List<PrintAggregationDTO>> aggregatedCodes(@PathVariable String id) {
        return ResponseEntity.ok(partyAggregationService.getForPrinting(id));
    }

    @GetMapping(value = "/export")
    public ResponseEntity<Void> getAggregationReportCsv(@ModelAttribute AggregationFilter filter) {
        partyAggregationService.exportReport(filter);
        return ResponseEntity.ok().build();
    }
}
