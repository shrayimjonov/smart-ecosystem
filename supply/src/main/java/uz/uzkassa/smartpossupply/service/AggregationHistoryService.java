package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationHistoryListDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponseResult;
import uz.uzkassa.smartpossupply.filters.ClickhouseFilter;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;
import uz.uzkassa.smartpossupply.repository.AggregationHistoryRepository;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AggregationHistoryService extends BaseService {

    AggregationHistoryRepository aggregationHistoryRepository;
    AslBelgiClient aslBelgiClient;

    public Page<AggregationHistoryListDTO> getList(ClickhouseFilter filter) {
        return aggregationHistoryRepository.getResultList(filter);
    }

    /**
     * There are two types of checking code in CRPT turon. CisInfo check and public check.
     * CisInfo check returns more information about the code. Public check with limited information.
     * But CisInfo check returns info only code belong to company itself, otherwise it returns empty array.
     * Public check this criteria does not matter.
     */
    public CisInfoResponseResult check(BatchValidationDTO validationDTO) {
        //Check Cis Info
        try {
            String token = Optional.ofNullable(validationDTO.getToken()).orElse(validateAslBelgiAndGetAggregationInfo().getAslBelgiToken());
            CisInfoResponseResult cisInfoResponseResult = aslBelgiClient.checkCisesInfo(
                    token,
                    validationDTO.getAggregationCodes(),
                    validationDTO.getMarkCodes(),
                    validationDTO.getAggregationBlockCodes(),
                    validationDTO.getProductType()
            );
            if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
                return cisInfoResponseResult;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_ERROR), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_ERROR));
    }

    public CisInfoResponseResult publicCheck(BatchValidationDTO validationDTO) {
        //Check Public Info
        try {
            String token = Optional.ofNullable(validationDTO.getToken()).orElse(validateAslBelgiAndGetAggregationInfo().getAslBelgiToken());
            CisInfoResponseResult cisInfoResponseResult = aslBelgiClient.checkAggregatedPublicList(
                    token,
                    validationDTO.getAggregationCodes(),
                    validationDTO.getMarkCodes(),
                    validationDTO.getAggregationBlockCodes(),
                    validationDTO.getProductType()
            );
            if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
                return cisInfoResponseResult;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_ERROR), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_ERROR));
    }

    /**
     * Check private info
     */
    public CisInfoResponseResult checkPrivateInfo(BatchValidationDTO validationDTO) {
        String token = Optional.ofNullable(validationDTO.getToken()).orElse(getXTraceInfo().getApiKey());
        CisInfoResponseResult cisInfoResponseResult = aslBelgiClient.checkPrivateInfo(
                token,
                validationDTO.getAggregationCodes(),
                validationDTO.getMarkCodes(),
                validationDTO.getAggregationBlockCodes(),
                validationDTO.getProductType()
        );
        if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
            return cisInfoResponseResult;
        }
        throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_XTRACE_ERROR), "Empty response");
    }

    /**
     * Check public info
     */
    public CisInfoResponseResult checkPublicInfo(BatchValidationDTO validationDTO) {
        String token = Optional.ofNullable(validationDTO.getToken()).orElse(getXTraceInfo().getApiKey());
        CisInfoResponseResult cisInfoResponseResult = aslBelgiClient.checkPublicInfo(
                token,
                validationDTO.getAggregationCodes(),
                validationDTO.getMarkCodes(),
                validationDTO.getAggregationBlockCodes(),
                validationDTO.getProductType()
        );
        if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
            return cisInfoResponseResult;
        }
        throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_XTRACE_ERROR), "Empty response");
    }
}