package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.aggregation.AggregationBlock;
import uz.uzkassa.smartpossupply.domain.mark.Mark;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrderParty;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.mark.MarkListDTO;
import uz.uzkassa.smartpossupply.dto.mark.MarkStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.payload.PartyUtilisationPayload;
import uz.uzkassa.smartpossupply.enums.MarkStatus;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.filters.AggregationBlockFilter;
import uz.uzkassa.smartpossupply.filters.MarkFilter;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;
import uz.uzkassa.smartpossupply.repository.AggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.MarkOrderPartyRepository;
import uz.uzkassa.smartpossupply.repository.MarkRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MarkService extends BaseService {

    MarkRepository markRepository;
    AggregationBlockRepository aggregationBlockRepository;
    MarkOrderPartyRepository markOrderPartyRepository;
    AslBelgiClient aslBelgiClient;

    public Page<MarkListDTO> getList(MarkFilter filter) {
        if (filter.getPartyId() != null) {
            MarkOrderParty markOrderParty = markOrderPartyRepository
                .findById(filter.getPartyId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(MarkOrderParty.class.getSimpleName(), "id", filter.getPartyId()));
            if (ProductType.isContainGroupMarkOrder(markOrderParty.getMarkOrderProduct().getProduct().getProductType())) {
                AggregationBlockFilter aggregationBlockFilter = new AggregationBlockFilter();
                aggregationBlockFilter.setMarkOrderPartyId(filter.getPartyId());
                aggregationBlockFilter.setUsed(filter.getUsed());
                ResultList<AggregationBlock> resultList = aggregationBlockRepository.getResultList(aggregationBlockFilter);
                List<MarkListDTO> result = resultList
                    .getList()
                    .stream()
                    .map(aggregationBlock -> {
                        MarkListDTO markListDTO = new MarkListDTO();
                        markListDTO.setId(aggregationBlock.getId());
                        markListDTO.setCode(aggregationBlock.getCode());
                        markListDTO.setPrintCode(aggregationBlock.getPrintCode());
                        markListDTO.setProduct(aggregationBlock.getProduct().toProductCommonDTO());
                        markListDTO.setAggregationStatus(aggregationBlock.getStatus());
                        markListDTO.setUsed(aggregationBlock.isUsed());
                        markListDTO.setTuronStatus(aggregationBlock.getTuronStatus());
                        return markListDTO;
                    })
                    .toList();
                return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
            }
        }

        ResultList<Mark> resultList = markRepository.getResultList(filter);
        List<MarkListDTO> result = resultList
            .getList()
            .stream()
            .map(mark -> {
                MarkListDTO markListDTO = new MarkListDTO();
                markListDTO.setId(mark.getId());
                markListDTO.setCode(mark.getCode());
                markListDTO.setPrintCode(mark.getOriginalCode());
                markListDTO.setProduct(mark.getProduct().toProductCommonDTO());
                markListDTO.setTuronStatus(mark.getTuronStatus());
                markListDTO.setUsed(mark.isUsed());
                return markListDTO;
            })
            .toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public MarkStatisticsDTO getStatistics(MarkFilter filter) {
        return markRepository.getStatistics(filter);
    }

    @Transactional
    public void removeFromAggregationGroupProduct(Long id) {
        markRepository.unsetPartyProductIdById(id);
    }

    @Transactional
    public void setAppliedByParty(PartyUtilisationPayload payload) {
        Set<String> marks = markRepository.findAllPrintCodesByMarkOrderPartyId(payload.getId());
        Map<String, TuronMarkStatus> cises = aslBelgiClient.checkMarksCisesInfo(payload.getClientToken(), marks);

        List<Mark> updatedMarkList = new LinkedList<>();
        for (String requestedCises : cises.keySet()) {
            Optional<Mark> markOptional = markRepository.findFirstByCode(MarkUtils.clearOriginalCode(requestedCises, payload.getProductType()));
            if (markOptional.isPresent()) {
                markOptional.get().setTuronStatus(MarkStatus.valueOf(cises.get(requestedCises).name()));
                updatedMarkList.add(markOptional.get());
            }
            if (updatedMarkList.size() > 50) {
                markRepository.saveAll(updatedMarkList);
            }
        }
        if (!CollectionUtils.isEmpty(updatedMarkList)) {
            markRepository.saveAll(updatedMarkList);
        }
    }
}
