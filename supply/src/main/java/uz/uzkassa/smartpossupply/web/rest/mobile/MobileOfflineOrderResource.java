package uz.uzkassa.smartpossupply.web.rest.mobile;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.offlineorder.OfflineOrderDetailDTO;
import uz.uzkassa.smartpossupply.dto.offlineorder.OfflineOrderListDTO;
import uz.uzkassa.smartpossupply.dto.offlineorder.OfflineOfflineOrderUpdateListDTO;
import uz.uzkassa.smartpossupply.service.OfflineOrderService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(ApiConstants.mobileOfflineOrderRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileOfflineOrderResource {

    OfflineOrderService offlineOrderService;

    @PostMapping
    public ResponseEntity<List<Long>> create(@RequestBody OfflineOrderListDTO offlineOrderListDTO) {
        return ResponseEntity.ok(offlineOrderService.create(offlineOrderListDTO));
    }

    @PutMapping
    public ResponseEntity<List<Long>> update(@RequestBody OfflineOfflineOrderUpdateListDTO offlineOrderUpdateListDTO) {
        return ResponseEntity.ok(offlineOrderService.update(offlineOrderUpdateListDTO));
    }

    @GetMapping
    public ResponseEntity<List<OfflineOrderDetailDTO>> getByMobileIds(@RequestParam List<String> mobileIds) {
        if (CollectionUtils.isEmpty(mobileIds)) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(offlineOrderService.getByMobileIds(mobileIds));
    }

}




