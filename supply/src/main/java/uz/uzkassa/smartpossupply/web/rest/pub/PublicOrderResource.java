package uz.uzkassa.smartpossupply.web.rest.pub;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.enums.*;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.publicOrderRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicOrderResource {

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<OrderStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(OrderStatus.class));
    }

    @Operation(summary = "Payment Statuses")
    @GetMapping("/payment-statuses")
    public ResponseEntity<EnumSet<OrderPaymentStatus>> getPaymentStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(OrderPaymentStatus.class));
    }

    @Operation(summary = "Payment Types")
    @GetMapping(ApiConstants.paymentTypes)
    public ResponseEntity<EnumSet<PaymentType>> getPaymentTypes() {
        return ResponseEntity.ok(EnumSet.of(PaymentType.CASH,PaymentType.CARD,PaymentType.APAY,PaymentType.TRANSFER));
    }

    @GetMapping("/payment-types/offline")
    public ResponseEntity<EnumSet<PaymentType>> getPaymentTypesForOffline() {
        return ResponseEntity.ok(EnumSet.of(PaymentType.CASH,PaymentType.CARD,PaymentType.APAY,PaymentType.TRANSFER));
    }

    @Operation(summary = "Sale Types")
    @GetMapping("/sale-types")
    public ResponseEntity<EnumSet<SaleType>> getSaleTypes() {
        return ResponseEntity.ok(EnumSet.allOf(SaleType.class));
    }

    @Operation(summary = "Currency Types")
    @GetMapping("/currency-types")
    public ResponseEntity<EnumSet<CurrencyType>> getCurrencyTypes() {
        return ResponseEntity.ok(EnumSet.allOf(CurrencyType.class));
    }

    @Operation(summary = ApiConstants.Types)
    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<OrderType>> getOrderTypes() {
        return ResponseEntity.ok(EnumSet.allOf(OrderType.class));
    }

    @Operation(summary = "Order souress")
    @GetMapping("/sources")
    public ResponseEntity<EnumSet<OrderSource>> getOrderSources() {
        return ResponseEntity.ok(EnumSet.allOf(OrderSource.class));
    }

    @Operation(summary = "Stock transfer types")
    @GetMapping("/stock-transfer-types")
    public ResponseEntity<EnumSet<StockTransferType>> getStockTransferTypes() {
        return ResponseEntity.ok(EnumSet.allOf(StockTransferType.class));
    }
}
