package uz.uzkassa.horeca.web.rest.pub;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.qrmenu.QrMenuCompanyDetails;
import uz.uzkassa.horeca.dto.qrmenu.QrMenuProducts;
import uz.uzkassa.horeca.service.QrMenuService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.publicQrMenuRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicQrMenuResource {
    QrMenuService qrMenuService;

    @GetMapping("/company/{uri}")
    public ResponseEntity<QrMenuCompanyDetails> companyDetails(@PathVariable String uri) {
        return ResponseEntity.ok(qrMenuService.getCompanyDetails(uri));
    }

    @GetMapping("/categories/{uri}")
    public ResponseEntity<List<CommonDTO>> getCategories(@PathVariable String uri) {
        return ResponseEntity.ok(qrMenuService.getCategories(uri));
    }

    @GetMapping("/products/{uri}")
    public ResponseEntity<List<QrMenuProducts>> getProducts(@PathVariable String uri) {
        return ResponseEntity.ok(qrMenuService.getProducts(uri));
    }


}
