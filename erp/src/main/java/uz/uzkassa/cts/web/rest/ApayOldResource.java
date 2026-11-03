package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.apay.ApayCustomerDetailDTO;
import uz.uzkassa.cts.dto.apay.ApayTariffDTO;
import uz.uzkassa.cts.integrations.ApayClient;
import uz.uzkassa.cts.service.APayService;

import java.util.List;

@RestController
@RequestMapping("/api/apay")
@Tag(name = "APay")
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ApayOldResource {

    ApayClient apayClient;
    APayService aPayService;

    @Operation(summary = "Customer By TIN")
    @GetMapping("/customers/by-tin/{tin}")
    public ResponseEntity<ApayCustomerDetailDTO> getCustomerByTin(@PathVariable(name = "tin") String tin) {
        return ResponseEntity.ok(aPayService.getCustomerByTin(tin));
    }

    @GetMapping("/tariffs")
    public ResponseEntity<List<ApayTariffDTO>> tariffs() {
        return apayClient.getTariffs();
    }

    @GetMapping("/tariffs/{id}")
    public ResponseEntity<ApayTariffDTO> tariff(@PathVariable String id) {
        return apayClient.tariffById(id);
    }
}
