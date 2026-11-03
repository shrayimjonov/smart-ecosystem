package uz.uzkassa.smartpossupply.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.billing.UsagePlan;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyAggregationInfo;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrder;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrderProduct;
import uz.uzkassa.smartpossupply.dto.integration.turon.mark.TuronMarkOrderDTO;
import uz.uzkassa.smartpossupply.dto.integration.turon.mark.TuronMarkOrderProductDTO;
import uz.uzkassa.smartpossupply.dto.integration.turon.mark.TuronMarkOrderResponseDTO;
import uz.uzkassa.smartpossupply.dto.markorder.MarkOrderDTO;
import uz.uzkassa.smartpossupply.dto.markorder.MarkOrderProductItemDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.integration.TuronClient;
import uz.uzkassa.smartpossupply.numbering.NumberingContext;
import uz.uzkassa.smartpossupply.repository.MarkOrderRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MarkOrderService extends BaseService {

    MarkOrderRepository markOrderRepository;
    ProductRepository productRepository;
    TuronClient turonClient;
    NumberingContext numberingContext;
    UsagePlanService usagePlanService;

    @Transactional
    public Long create(MarkOrderDTO markOrderDTO) {
        for (MarkOrderProductItemDTO markOrderProductItemDTO : markOrderDTO.getProducts()) {
            if (SerialNumberType.SELF_MADE == markOrderProductItemDTO.getSerialNumberType()) {
                if (CollectionUtils.isEmpty(markOrderProductItemDTO.getSerialNumbers())) {
                    throw validationService.badRequestExceptionThrow("В данном тип заказа должен быть заполнен коды").get();
                }
                if (markOrderProductItemDTO.getSerialNumbers().size() != markOrderProductItemDTO.getQty()) {
                    throw validationService.badRequestExceptionThrow("Количество не правилно").get();
                }
            }
        }
        Company company = getCurrentCompany();
        CompanyAggregationInfo companyAggregationInfo = validateTuronAndGetAggregationInfo();
        UsagePlan usagePlan = null;
        int countMarkOrder = markOrderDTO.getProducts().stream().mapToInt(MarkOrderProductItemDTO::getQty).sum();
        if (!company.isFreeTrial()) {
            usagePlan = usagePlanService.getUsagePlan(CountableServiceTypes.MARK_ORDER.getCode());
            if (usagePlan.getQty() <= 0 || usagePlan.getQty() < countMarkOrder) {
                throw validationService.badRequestExceptionThrow("Недостаточно средств для заказа маркировок", "Недостаточно средств для заказа маркировок").get();
            }
        }

        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.MARK_ORDER, getCurrentCompanyId());
        MarkOrder markOrder = new MarkOrder();
        markOrder.setOrderNumber(numberingMap.get(NUMBER));
        markOrder.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        markOrder.setCompanyId(company.getId());
        markOrder.setReleaseMethodType(Optional.ofNullable(markOrderDTO.getReleaseMethodType()).orElse(MarkOrderReleaseMethodType.IMPORT));
        markOrder.setCreateMethodType(MarkOrderCreateMethodType.SELF_MADE);
        markOrderRepository.save(markOrder);

        TuronMarkOrderDTO turonMarkOrderDTO = new TuronMarkOrderDTO();
        turonMarkOrderDTO.setContactPerson(companyAggregationInfo.getContactPerson());
        turonMarkOrderDTO.setCreateMethodType(markOrder.getCreateMethodType().name());
        turonMarkOrderDTO.setReleaseMethodType(markOrder.getReleaseMethodType().name());

        if (ProductGroup.Tobacco == company.getProductGroup()) {
            turonMarkOrderDTO.setFactoryId(companyAggregationInfo.getFactoryId());
            turonMarkOrderDTO.setFactoryCountry(companyAggregationInfo.getFactoryCountry());
            turonMarkOrderDTO.setProductionLineId(company.getTin());
            if (company.getName().length() > 20) {
                turonMarkOrderDTO.setProductDescription(company.getName().substring(0, 20));
            } else {
                turonMarkOrderDTO.setProductDescription(company.getName());
            }
            turonMarkOrderDTO.setProductCode(company.getTin());
        }

        markOrderDTO.getProducts().forEach(productItemDTO -> {
            Product product = productRepository
                .findById(productItemDTO.getProductId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", productItemDTO.getProductId()));
            if (product.getProductType() == null || product.getProductType().getTemplateId() == null) {
                throw validationService.badRequestExceptionThrow("Продукт ".concat(product.getName()).concat(" не имеет типа")).get();
            }
            MarkOrderProduct markOrderProduct = new MarkOrderProduct();
            markOrderProduct.setProductId(product.getId());
            markOrderProduct.setProduct(product);
            markOrderProduct.setQty(productItemDTO.getQty());
            markOrderProduct.setRemaining(productItemDTO.getQty());
            markOrderProduct.setRateType(Optional.ofNullable(productItemDTO.getRateType()).orElse(MarkOrderRateType.STANDARD));
            markOrderProduct.setSerialNumberType(Optional.ofNullable(productItemDTO.getSerialNumberType())
                .orElse(SerialNumberType.OPERATOR));
            if (ProductType.isContainGroupMarkOrder(product.getProductType())) {
                markOrderProduct.setCisType(CisType.GROUP);
            } else {
                markOrderProduct.setCisType(CisType.UNIT);
            }
            markOrder.addProduct(markOrderProduct);

            TuronMarkOrderProductDTO turonMarkOrderProductDTO = new TuronMarkOrderProductDTO();
            turonMarkOrderProductDTO.setGtin(MarkUtils.generateGTIN(markOrderProduct.getProduct().getBarcode()));
            turonMarkOrderProductDTO.setQuantity(markOrderProduct.getQty());
            turonMarkOrderProductDTO.setTemplateId(markOrderProduct.getProduct().getProductType().getTemplateId());
            turonMarkOrderProductDTO.setCisType(markOrderProduct.getCisType().name());
            turonMarkOrderProductDTO.setRateType(markOrderProduct.getRateType().getRate());
            turonMarkOrderProductDTO.setSerialNumberType(markOrderProduct.getSerialNumberType().name());
            if (SerialNumberType.SELF_MADE == markOrderProduct.getSerialNumberType()) {
                turonMarkOrderProductDTO.setSerialNumbers(productItemDTO.getSerialNumbers());
            }
            turonMarkOrderDTO.addProduct(turonMarkOrderProductDTO);
        });

        TuronMarkOrderResponseDTO responseDTO = turonClient.createMarkOrder(
            turonMarkOrderDTO,
            company.getProductGroup().getExtension(),
            companyAggregationInfo.getTuronToken(),
            companyAggregationInfo.getOmsId()
        );
        markOrder.setTuronId(responseDTO.getOrderId());
        markOrder.setExpectedCompleteTime(responseDTO.getExpectedCompleteTimestamp());
        markOrderRepository.save(markOrder);
        if (usagePlan != null) {
            usagePlan.setQty(usagePlan.getQty() - countMarkOrder);
            usagePlanRepository.save(usagePlan);
        }
        return markOrder.getId();
    }

    public Set<String> readFromFile(MultipartFile file) throws IOException, CsvValidationException {
        Set<String> codes = new LinkedHashSet<>();
        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
        String[] values;
        while ((values = csvReader.readNext()) != null) {
            codes.add(values[0]);
        }
        return codes;
    }

    public EnumSet<MarkOrderReleaseMethodType> getMarkOrderReleaseMethodType() {
        if (CompanyType.MANUFACTURER == getCurrentCompany().getCompanyType()) {
            return EnumSet.of(MarkOrderReleaseMethodType.PRODUCTION, MarkOrderReleaseMethodType.COMMISSION, MarkOrderReleaseMethodType.REMAINS);
        } else {
            return EnumSet.of(MarkOrderReleaseMethodType.IMPORT, MarkOrderReleaseMethodType.COMMISSION, MarkOrderReleaseMethodType.REMAINS);
        }
    }
}
