package uz.uzkassa.horeca.web.rest.pub;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonLocalizedDTO;
import uz.uzkassa.horeca.dto.eps.EpsServiceDTO;
import uz.uzkassa.horeca.dto.factura.PlatformType;
import uz.uzkassa.horeca.enums.*;
import uz.uzkassa.horeca.service.EpsService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.publicCommonRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicCommonResource {
    EpsService epsService;

    @GetMapping("/vats")
    public ResponseEntity<EnumSet<Vat>> getVats() {
        return ResponseEntity.ok(EnumSet.allOf(Vat.class));
    }

    @GetMapping("/cooking/types")
    public ResponseEntity<EnumSet<CookingType>> getCookingTypes() {
        return ResponseEntity.ok(EnumSet.allOf(CookingType.class));
    }

    @GetMapping("/business/types")
    public ResponseEntity<EnumSet<BusinessType>> getBusinessTypes() {
        return ResponseEntity.ok(EnumSet.allOf(BusinessType.class));
    }

    @GetMapping("/branch/statuses")
    public ResponseEntity<EnumSet<CommonStatus>> getBranchStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

    @GetMapping("/genders")
    public ResponseEntity<EnumSet<Gender>> getGenders() {
        return ResponseEntity.ok(EnumSet.allOf(Gender.class));
    }

    @GetMapping("/age-groups")
    public ResponseEntity<EnumSet<AgeGroup>> getAgeGroups() {
        return ResponseEntity.ok(EnumSet.allOf(AgeGroup.class));
    }

    @GetMapping("/user/statuses")
    public ResponseEntity<EnumSet<UserStatus>> getUserStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(UserStatus.class));
    }

    @GetMapping("/order/statuses")
    public ResponseEntity<EnumSet<OrderStatus>> getOrderStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(OrderStatus.class));
    }

    @GetMapping("/order/item/statuses")
    public ResponseEntity<EnumSet<OrderItemStatus>> getOrderItemStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(OrderItemStatus.class));
    }

    @GetMapping("/order/reasons")
    public ResponseEntity<EnumSet<OrderReason>> getOrderReasons() {
        return ResponseEntity.ok(EnumSet.allOf(OrderReason.class));
    }

    @GetMapping("/reservation/statuses")
    public ResponseEntity<EnumSet<ReservationStatus>> getReservationStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(ReservationStatus.class));
    }

    @GetMapping("/payment/statuses")
    public ResponseEntity<EnumSet<PaymentStatus>> getPaymentStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(PaymentStatus.class));
    }

    @GetMapping("/payment/types")
    public ResponseEntity<EnumSet<PaymentType>> getPaymentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(PaymentType.class));
    }

    @GetMapping("/department/statuses")
    public ResponseEntity<EnumSet<CommonStatus>> getDepartmentStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

    @GetMapping("/department/types")
    public ResponseEntity<EnumSet<DepartmentType>> getDepartmentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DepartmentType.class));
    }

    @GetMapping("/hall-items/statuses")
    public ResponseEntity<EnumSet<HallItemStatus>> getHallItemStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(HallItemStatus.class));
    }

    @GetMapping("/hall-items/types")
    public ResponseEntity<EnumSet<HallItemType>> getHallItemTypes() {
        return ResponseEntity.ok(EnumSet.allOf(HallItemType.class));
    }

    @GetMapping("/hall-items/shapes")
    public ResponseEntity<EnumSet<ShapeType>> getHallItemShapes() {
        return ResponseEntity.ok(EnumSet.allOf(ShapeType.class));
    }

    @GetMapping("/hall-items/reasons")
    public ResponseEntity<EnumSet<HallItemReason>> getHallItemReasons() {
        return ResponseEntity.ok(EnumSet.allOf(HallItemReason.class));
    }

    @GetMapping("/category/types")
    public ResponseEntity<EnumSet<CategoryType>> getCategoryTypes() {
        return ResponseEntity.ok(EnumSet.allOf(CategoryType.class));
    }

    @GetMapping("/stock/statuses")
    public ResponseEntity<EnumSet<StockStatus>> getStockStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(StockStatus.class));
    }

    @GetMapping("/receipt/payment-types")
    public ResponseEntity<List<CommonLocalizedDTO>> receiptPaymentTypes() {
        return ResponseEntity.ok(ReceiptPaymentType.getList());
    }

    @GetMapping("/product/origins")
    public ResponseEntity<EnumSet<ProductOrigin>> getProductOrigins() {
        return ResponseEntity.ok(EnumSet.allOf(ProductOrigin.class));
    }

    @GetMapping("/product/types")
    public ResponseEntity<EnumSet<ProductType>> getProductTypes() {
        return ResponseEntity.ok(EnumSet.allOf(ProductType.class));
    }

    @GetMapping("/receipt/statuses")
    public ResponseEntity<EnumSet<ReceiptStatus>> getReceiptStatus() {
        return ResponseEntity.ok(EnumSet.allOf(ReceiptStatus.class));
    }

    @GetMapping("/platform-types")
    public ResponseEntity<EnumSet<PlatformType>> getPlatformTypes() {
        return ResponseEntity.ok(EnumSet.allOf(PlatformType.class));
    }

    @GetMapping("/eps-service")
    public ResponseEntity<List<EpsServiceDTO>> getList() {
        return ResponseEntity.ok(epsService.getList());
    }
}
