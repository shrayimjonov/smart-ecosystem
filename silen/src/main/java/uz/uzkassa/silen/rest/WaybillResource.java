package uz.uzkassa.silen.rest;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.EnumDTO;
import uz.uzkassa.silen.dto.Result;
import uz.uzkassa.silen.dto.WaybillListDTO;
import uz.uzkassa.silen.dto.filter.CustomerFilter;
import uz.uzkassa.silen.dto.filter.WaybillFilter;
import uz.uzkassa.silen.dto.waybill.*;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.service.WaybillService;
import uz.uzkassa.silen.utils.ServerUtils;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/waybill")
@RequiredArgsConstructor
public class WaybillResource {
    private final WaybillService waybillService;


    @PostMapping
    public ResponseEntity<String> save(@Valid @RequestBody WaybillDTO waybillDTO) {
        return ResponseEntity.ok(waybillService.create(waybillDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable String id, @RequestBody @Valid WaybillDTO waybillDTO) {
        return ResponseEntity.ok(waybillService.update(id, waybillDTO));
    }

    @GetMapping
    public ResponseEntity<Page<WaybillListDTO>> findAllByFilter(@ModelAttribute WaybillFilter filter) {
        return ResponseEntity.ok(waybillService.findAllByFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaybillDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(waybillService.getOne(id));
    }

    @PutMapping("/invoice/{id}")
    public ResponseEntity<String> createFromInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(waybillService.convertInvoice(id));
    }

    @PutMapping("/act/{id}")
    public ResponseEntity<String> createFromAct(@PathVariable String id) {
        return ResponseEntity.ok(waybillService.createFromAct(id));
    }

    @PostMapping("/send")
    public ResponseEntity<Result<Void>> send(@RequestBody @Valid WaybillSendDTO waybillSendDTO) {
        waybillSendDTO.setClientIp(ServerUtils.getCurrentUserIP());
        waybillService.send(waybillSendDTO);
        return ResponseEntity.ok(Result.success());
    }

    @PostMapping("/cancel")
    public ResponseEntity<Result<Void>> cancel(@Valid @RequestBody final WaybillSendDTO dto) {
        dto.setClientIp(ServerUtils.getCurrentUserIP());
        waybillService.cancel(dto);
        return ResponseEntity.ok(Result.success());
    }

    @PostMapping("/accept")
    public ResponseEntity<Result<Void>> accept(@Valid @RequestBody final WaybillSendDTO dto) {
        dto.setWaybillLocalSignType("ConsigneeAccepted");
        dto.setClientIp(ServerUtils.getCurrentUserIP());
        waybillService.acceptOrReject(dto, WaybillStatus.ConsigneeAccepted);
        return ResponseEntity.ok(Result.success());
    }

    @PostMapping("/reject")
    public ResponseEntity<Result<Void>> reject(@Valid @RequestBody final WaybillSendDTO dto) {
        dto.setWaybillLocalSignType("ConsigneeRejected");
        dto.setClientIp(ServerUtils.getCurrentUserIP());
        waybillService.acceptOrReject(dto, WaybillStatus.ConsigneeRejected);
        return ResponseEntity.ok(Result.success());
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<EnumDTO>> getStatuses() {
        return ResponseEntity.ok(WaybillStatus.getAll());
    }

    @GetMapping("/trailer-types")
    public ResponseEntity<List<EnumDTO>> getTrailerTypes() {
        return ResponseEntity.ok(TrailerType.getAll());
    }

    @GetMapping("/delivery-types")
    public ResponseEntity<List<EnumDTO>> getDeliveryTypes() {
        return ResponseEntity.ok(DeliveryType.getAll());
    }

    @GetMapping("/transport-types")
    public ResponseEntity<List<EnumDTO>> getTransportTypes() {
        return ResponseEntity.ok(TransportType.getAll());
    }

    @GetMapping("/types")
    public ResponseEntity<List<EnumDTO>> getTypes() {
        return ResponseEntity.ok(WaybillType.getAll());
    }

    @GetMapping("/local-types")
    public ResponseEntity<List<EnumDTO>> getLocalTypes() {
        return ResponseEntity.ok(WaybillLocalType.getAll());
    }

    @GetMapping("/carriage/{tin}")
    public ResponseEntity<List<CompanyTransportDTO>> getCompanyCarriages(@PathVariable String tin) {
        return ResponseEntity.ok(waybillService.getCompanyTransports(tin, EnumSet.of(WaybillTransportType.Carriage)));
    }

    @GetMapping("/trailer/{tin}")
    public ResponseEntity<List<CompanyTransportDTO>> getCompanyTrailers(@PathVariable String tin) {
        return ResponseEntity.ok(waybillService.getCompanyTransports(tin, EnumSet.of(WaybillTransportType.Trailer)));
    }

    @GetMapping("/truck/{tin}")
    public ResponseEntity<List<CompanyTransportDTO>> getCompanyTrucks(@PathVariable String tin) {
        return ResponseEntity.ok(waybillService.getCompanyTransports(tin, EnumSet.complementOf(EnumSet.of(WaybillTransportType.Trailer, WaybillTransportType.Carriage))));
    }

    @GetMapping("/empowerments/{tin}")
    public ResponseEntity<EmpowermentDTO[]> getCompanyEmpowerments(@PathVariable String tin) {
        return ResponseEntity.ok(waybillService.getCompanyEmpowerments(tin));
    }

    @GetMapping("/customers")
    public ResponseEntity<List<WaybillCustomerDTO>> getCompanyCustomers(@ModelAttribute CustomerFilter filter) {
        return ResponseEntity.ok(waybillService.getCompanyCustomers(filter));
    }

    @GetMapping("/driver/{tinOrPinfl}")
    public ResponseEntity<PhysicalPersonDTO> getDriver(@PathVariable String tinOrPinfl) {
        return ResponseEntity.ok(waybillService.getPhysicalPersonData(tinOrPinfl));
    }

    @GetMapping("/stations")
    public ResponseEntity<Object> getStations() {
        return ResponseEntity.ok(waybillService.getStations());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        waybillService.delete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/product-group/{id}")
    public ResponseEntity<Void> deleteProductGroup(@PathVariable String id) {
        waybillService.deleteProductGroup(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        waybillService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
