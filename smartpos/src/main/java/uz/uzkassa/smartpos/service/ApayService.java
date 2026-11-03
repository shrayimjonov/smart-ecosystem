package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDetailDTO;
import uz.uzkassa.smartpos.enums.BillingType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.integration.apay.ApayClient;
import uz.uzkassa.smartpos.integration.apay.request.ApayCreateBillRequest;
import uz.uzkassa.smartpos.integration.apay.request.ApayVoucherRequest;
import uz.uzkassa.smartpos.integration.apay.response.ApayCreateBillResponse;
import uz.uzkassa.smartpos.integration.apay.response.ApayVoucherResponse;
import uz.uzkassa.smartpos.integration.apay.response.VoucherDTO;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ApayService extends SmartposService {
    private final ApayClient apayClient;
    private final ProductBranchRepository productBranchRepository;
    private final TokenStoreService tokenStoreService;

    public ApayCreateBillResponse createBill(ApayCreateBillRequest request) {
        if (BigDecimal.valueOf(100).compareTo(request.getAmount()) > 0) {
            throw new BadRequestException("Минимальная сумма оплаты 100.00");
        }
        String branchId = StringUtils.isNotEmpty(getCurrentBranchIdByHeader()) ? getCurrentBranchIdByHeader() : String.valueOf(getCurrentBranchId());
        Long ctoId = branchRepository.findById(Long.valueOf(branchId)).map(Branch::getCtoId).orElse(null);
        request.setReferenceId(branchId);
        request.setCtoId(ctoId);
        request.setReferenceType(BillingType.SMARTPOS);
        return apayClient.createBill(tokenStoreService.getToken(TokenType.APAY), request).getBody();
    }

    public VoucherDTO getVoucher(ApayVoucherRequest dto) {
        final Long branchId = getCurrentBranchId();
        dto.setMerchantId(String.valueOf(branchId));
        ResponseEntity<ApayVoucherResponse> response = apayClient.getVoucher(tokenStoreService.getToken(TokenType.APAY), dto);
        if (HttpStatus.OK != response.getStatusCode()) {
            throw badRequest("Ошибка при получении ваучера :" + response.getBody()).get();
        }
        ApayVoucherResponse voucherResponse = response.getBody();
        final Long cityId = getBranchCityId(branchId);
        List<ProductBranch> products = null;
        if (voucherResponse != null) {
            products = productBranchRepository
                    .findAllByBarcodeInAndBranchIdAndCityIdAndDeletedIsFalse(voucherResponse.getDetails().stream().map(ApayVoucherResponse.VoucherDetail::getProductBarCode).collect(Collectors.toList()), branchId, cityId);
        }
        if (CollectionUtils.isEmpty(products)) {
            throw new EntityNotFound(localizationService.getMessage("product"));
        }
        Map<String, Integer> productQuantityMap = voucherResponse.getDetails().stream().collect(Collectors.toMap(ApayVoucherResponse.VoucherDetail::getProductBarCode, ApayVoucherResponse.VoucherDetail::getProductQuantity));
        VoucherDTO result = new VoucherDTO();
        result.setPhone(voucherResponse.getClientPhone());
        List<ReceiptDetailDTO> detailDTOS = new ArrayList<>();
        products.forEach(productBranch -> {
            ReceiptDetailDTO product = new ReceiptDetailDTO();
            product.setCategoryId(productBranch.getCategoryId());
            product.setProductBarcode(productBranch.getBarcode());
            product.setProductId(productBranch.getProductId());
            product.setProductName(productBranch.getName());
            product.setProductBarcode(productBranch.getBarcode());
            product.setVatBarcode(productBranch.getVatBarcode());
            product.setUnitId(productBranch.getUnitId());
            if (productBranch.getUnit() != null) {
                product.setUnitName(productBranch.getUnit().getName());
            }
            product.setNdsPercent(productBranch.getVatRate());
            product.setExcise(productBranch.getExciseAmount());
            product.setCommittentTin(productBranch.getCommittentTin());
            product.setPrice(productBranch.getSalesPrice());
            product.setQty(BigDecimal.valueOf(productQuantityMap.get(product.getProductBarcode())));
            detailDTOS.add(product);
        });
        result.setProducts(detailDTOS);
        return result;
    }
}
