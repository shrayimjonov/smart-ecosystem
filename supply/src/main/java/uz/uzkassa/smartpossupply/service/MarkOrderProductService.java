package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.company.CompanyAggregationInfo;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrderProduct;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.integration.turon.mark.TuronMarkOrderCheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.markorder.MarkOrderProductListDTO;
import uz.uzkassa.smartpossupply.enums.BufferStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.integration.TuronClient;
import uz.uzkassa.smartpossupply.repository.MarkOrderProductRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MarkOrderProductService extends BaseService {

    MarkOrderProductRepository markOrderProductRepository;
    TuronClient turonClient;

    public Page<MarkOrderProductListDTO> getList(BaseFilter filter) {
        ResultList<MarkOrderProduct> resultList = markOrderProductRepository.getResultList(filter);
        List<MarkOrderProductListDTO> result = resultList
            .getList()
            .stream()
            .map(markOrderProduct -> {
                MarkOrderProductListDTO markOrderProductListDTO = new MarkOrderProductListDTO();
                markOrderProductListDTO.setId(markOrderProduct.getId());
                markOrderProductListDTO.setProduct(markOrderProduct.getProduct().toProductCommonDTO());
                markOrderProductListDTO.setOrder(markOrderProduct.getMarkOrder().toCommonDTO());
                markOrderProductListDTO.setReceived(markOrderProduct.getReceived());
                markOrderProductListDTO.setRemaining(markOrderProduct.getRemaining());
                markOrderProductListDTO.setQty(markOrderProduct.getQty());
                markOrderProductListDTO.setOrderDate(markOrderProduct.getMarkOrder().getCreatedDate());
                markOrderProductListDTO.setStatus(markOrderProduct.getBufferStatus());
                return markOrderProductListDTO;
            })
            .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void checkStatus(BufferStatus status) {
        List<MarkOrderProduct> markOrderProducts;
        if (BufferStatus.PENDING.equals(status)) {
            markOrderProducts = markOrderProductRepository.findAllByBufferStatusIsNullOrBufferStatus(status);
        } else {
            markOrderProducts = markOrderProductRepository.findAllByBufferStatus(status);
        }
        markOrderProducts.forEach(markOrderProduct -> {
            try {
                CompanyAggregationInfo companyAggregationInfo = companyAggregationInfoRepository.findByCompanyId(markOrderProduct.getMarkOrder().getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanyAggregationInfo.class.getSimpleName(), "companyId", markOrderProduct.getMarkOrder().getCompanyId()));
                updateOrderStatus(markOrderProduct, markOrderProduct.getMarkOrder().getTuronId(), companyAggregationInfo);
            } catch (Exception e) {
                log.error("Turon update status error:{}", e.getMessage(), e);
            }
        });
    }

    private void updateOrderStatus(MarkOrderProduct markOrderProduct, String turonId, CompanyAggregationInfo companyAggregationInfo) {
        String gtin = MarkUtils.generateGTIN(markOrderProduct.getProduct().getBarcode());
        TuronMarkOrderCheckStatusResponseDTO responseDTO = turonClient.checkStatus(
            markOrderProduct.getProduct().getProductType().getProductGroup().getExtension(),
            turonId,
            gtin,
            companyAggregationInfo.getTuronToken(),
            companyAggregationInfo.getOmsId()
        );
        if (responseDTO != null) {
            markOrderProduct.setBufferStatus(BufferStatus.valueOf(responseDTO.getBufferStatus()));
        } else {
            markOrderProduct.setBufferStatus(BufferStatus.CLOSED);
        }
        markOrderProductRepository.save(markOrderProduct);
    }
}
