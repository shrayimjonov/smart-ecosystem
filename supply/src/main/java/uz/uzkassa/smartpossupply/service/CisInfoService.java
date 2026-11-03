package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponseResult;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationBlockRepository;
import uz.uzkassa.smartpossupply.repository.StockProductAggregationRepository;
import uz.uzkassa.smartpossupply.repository.StockProductMarkRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;
import uz.uzkassa.smartpossupply.web.rest.errors.XtraceException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PROTECTED;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.12.2023
 */

/**
 * This class is the main class for checking codes from Asl Belgi.
 * Methods in this class are used by Stock in, out, return, transfer and adjustment processes.
 * Be careful when you change this class.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class CisInfoService extends BaseService {

    AslBelgiClient aslBelgiClient;
    StockProductAggregationRepository stockProductAggregationRepository;
    StockProductMarkRepository stockProductMarkRepository;
    StockProductAggregationBlockRepository stockProductAggregationBlockRepository;

    public void batchCheckAggregation(BatchValidationDTO validationDTO, Map<String, CisInfoResponse> resultMap, List<String> codes) {
        this.batchCheck(validationDTO, resultMap, codes, CodeType.AGGREGATION);
    }

    public void batchCheckMark(BatchValidationDTO validationDTO, Map<String, CisInfoResponse> resultMap, List<String> codes) {
        this.batchCheck(validationDTO, resultMap, codes, CodeType.MARK);
    }

    public void batchCheckAggregationBlock(BatchValidationDTO validationDTO, Map<String, CisInfoResponse> resultMap, List<String> codes) {
        this.batchCheck(validationDTO, resultMap, codes, CodeType.BLOCK);
    }

    private void batchCheck(BatchValidationDTO validationDTO, Map<String, CisInfoResponse> resultMap, List<String> codes, CodeType codeType) {
        CompanySettings xtraceSettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.XTRACE_ENABLED).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanySettings.class.getSimpleName(), "companyId", getCurrentCompanyId()));
        String token = null;
        if (xtraceSettings.isEnabled()) {
            if (StringUtils.isNotEmpty(validationDTO.getToken())) {
                token = validationDTO.getToken();
            } else {
                token = getXTraceInfo().getApiKey();
            }
        }

        if (StringUtils.isEmpty(token)) {
            throw new BadRequestException(localizationService.localize(MessageConstants.ORGANIZATION_TOKEN_EMPTY_ERROR));
        }

        //Check Public Info
        if (!codes.isEmpty()) {
            try {
                CisInfoResponseResult cisInfoResponseResult = null;
                if (xtraceSettings.isEnabled()) {
                    log.info("[stock-out/check] STEP 2 - sending to Asl Belgisi | companyId={} | codeType={} | productType={} | barcode={} | codes={}",
                            getCurrentCompanyId(), codeType, validationDTO.getProductType(), validationDTO.getBarcode(), codes);
                    cisInfoResponseResult = aslBelgiClient.checkPrivateInfo(token, codes, codeType, validationDTO.getProductType());
                    log.info("[stock-out/check] STEP 3 - Asl Belgisi result | codeType={} | response={}",
                            codeType, cisInfoResponseResult == null ? null : cisInfoResponseResult.getCisInfoResponses());
                }

                if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
                    for (CisInfoResponse cisInfoResponse : cisInfoResponseResult.getCisInfoResponses()) {
                        if (cisInfoResponse.getCisInfo() != null) {
                            PackageType packageType = MarkUtils.findPackageType(cisInfoResponse.getCisInfo());
                            if (packageType != null) {
                                List<String> children = null;
                                if (!CollectionUtils.isEmpty(cisInfoResponse.getCisInfo().getChild())) {
                                    children = cisInfoResponse.getCisInfo().getChild();
                                } else if (!CollectionUtils.isEmpty(cisInfoResponse.getCisInfo().getCodes())) {
                                    children = new ArrayList<>(cisInfoResponse.getCisInfo().getCodes().keySet());
                                }
                                if (PackageType.BLOCK.equals(packageType) || PackageType.LEVEL1.equals(packageType) || PackageType.GROUP.equals(packageType)) {
                                    if (!CollectionUtils.isEmpty(children)) {
                                        cisInfoResponse.getCisInfo().setQty(BigDecimal.valueOf(children.size()));
                                    } else {
                                        cisInfoResponse.getCisInfo().setQty(BigDecimal.ONE);
                                    }
                                }
                                if ((PackageType.BOX.equals(packageType) || PackageType.LEVEL2.equals(packageType) || PackageType.BOX_LV_1.equals(packageType)) && !CollectionUtils.isEmpty(children)) {
                                    cisInfoResponse.getCisInfo().setQty(BigDecimal.valueOf(children.size()));
                                }

                                if (PackageType.UNIT.equals(packageType) || PackageType.BOTTLE.equals(packageType)) {
                                    cisInfoResponse.getCisInfo().setQty(BigDecimal.ONE);
                                }
                            }
                        }
                        this.validateGtin(cisInfoResponse, codeType, validationDTO, resultMap, codes);
                        for (CisInfoResponse response : new ArrayList<>(resultMap.values())) {
                            if (response.getCisInfo().getStatus() == null && StringUtils.isEmpty(response.getCisInfo().getCis())) {
                                resultMap.remove(response.getCisInfo().getOriginalCode());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Asl Belgisi batchCheck failed | api=stock-out/check | companyId={} | codeType={} | productType={} | barcode={} | codes={} | aslBelgiError={}",
                        getCurrentCompanyId(), codeType, validationDTO.getProductType(), validationDTO.getBarcode(), codes, e.getMessage(), e);
                if (e instanceof XtraceException) {
                    throw new XtraceException(localizationService.localize(MessageConstants.ASL_BELGI_XTRACE_ERROR), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    throw new IntegrationException(localizationService.localize(MessageConstants.ASL_BELGI_ERROR), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
    }

    private void validateGtin(CisInfoResponse cisInfoResponse, CodeType codeType, BatchValidationDTO validationDTO, Map<String, CisInfoResponse> resultMap, List<String> codes) {
        if (cisInfoResponse.getCisInfo() != null && !codes.remove(cisInfoResponse.getCisInfo().getOriginalCode())) {
            if (!codes.remove(cisInfoResponse.getCisInfo().getRequestedCis())) {
                codes.remove(cisInfoResponse.getCisInfo().getCis());
            }
        }
        if (CodeType.AGGREGATION.equals(codeType) && StringUtils.isNotEmpty(validationDTO.getBarcode())) {
            String requestedCis = cisInfoResponse.getCisInfo().getRequestedCis();
            if (!resultMap.containsKey(requestedCis)) {
                resultMap.entrySet().stream()
                        .filter(entry -> entry.getKey().contains(requestedCis))
                        .findFirst()
                        .ifPresent(entry -> {
                            cisInfoResponse.getCisInfo().setOriginalCode(entry.getKey());
                            resultMap.remove(entry.getKey());
                        });
                resultMap.put(requestedCis, cisInfoResponse);
            }
            List<String> childCodes = null;
            if (!CollectionUtils.isEmpty(cisInfoResponse.getCisInfo().getChild())) {
                childCodes = cisInfoResponse.getCisInfo().getChild();
            } else if (!CollectionUtils.isEmpty(cisInfoResponse.getCisInfo().getCodes())) {
                childCodes = new ArrayList<>(cisInfoResponse.getCisInfo().getCodes().keySet());
            } else if (!CollectionUtils.isEmpty(cisInfoResponse.getCisInfo().getChildren())) {
                childCodes = new ArrayList<>(cisInfoResponse.getCisInfo().getChildren().keySet());
            }
            String code = CollectionUtils.isEmpty(childCodes) ? null : childCodes.getFirst();

            if (PackageType.isChosenBoxes(MarkUtils.findPackageType(cisInfoResponse.getCisInfo())) && StringUtils.isNotEmpty(code)) {
//                String validationBarcode = validationDTO.getBoxBarcode() != null ? validationDTO.getBoxBarcode() : validationDTO.getBlockBarcode();
//                boolean isBarcodeMatch = validationBarcode != null && !code.contains(validationBarcode) && !code.contains(validationDTO.getBarcode());

//                if (isBarcodeMatch) {
//                    log.info("1. FIRST BARCODE CHECKER : {}", code);
//                    updateCisInfoWithError(cisInfoResponse, validationDTO, resultMap, code);
//                } else {
                    updateCisInfo(cisInfoResponse, validationDTO, resultMap);
//                }
            } else if (!StringUtils.isEmpty(code) && !code.contains(validationDTO.getBarcode())
                    && !PackageType.isLevel3OrBoxLv2(MarkUtils.findPackageType(cisInfoResponse.getCisInfo()))) {
                updateCisInfoWithError(cisInfoResponse, validationDTO, resultMap, code);
            } else {
                updateCisInfo(cisInfoResponse, validationDTO, resultMap);
            }
            if (StringUtils.isEmpty(cisInfoResponse.getCisInfo().getOriginalCode())) {
                cisInfoResponse.getCisInfo().setOriginalCode(requestedCis);
            }
        } else {
            if (StringUtils.isNotEmpty(validationDTO.getBarcode()) && StringUtils.isEmpty(cisInfoResponse.getCisInfo().getGtin())) {
                cisInfoResponse.getCisInfo().setGtin(validationDTO.getBarcode());
            }
            resultMap.remove(cisInfoResponse.getCisInfo().getOriginalCode());
            resultMap.put(cisInfoResponse.getCisInfo().getOriginalCode(), cisInfoResponse);
        }
    }

    private void updateCisInfo(CisInfoResponse cisInfoResponse, BatchValidationDTO validationDTO, Map<String, CisInfoResponse> resultMap) {
        cisInfoResponse.getCisInfo().setGtin(validationDTO.getBarcode());
        String requestedCis = cisInfoResponse.getCisInfo().getRequestedCis();
        resultMap.remove(requestedCis);
        resultMap.put(requestedCis, cisInfoResponse);
    }

    private void updateCisInfoWithError(CisInfoResponse cisInfoResponse, BatchValidationDTO validationDTO, Map<String, CisInfoResponse> resultMap, String code) {
        CisInfo cisInfo = cisInfoResponse.getCisInfo();
        String errorMessage = localizationService.localize(MessageConstants.SELECTED_BARCODE_NOT_MATCH,
                new Object[]{validationDTO.getBarcode(), MarkUtils.getBarcodeFromMarkCode(code)});
        cisInfo.setStatus(TuronMarkStatus.ERROR);
        cisInfo.setErrorMessage(errorMessage);
        cisInfo.setGtin(validationDTO.getBarcode());
        resultMap.remove(cisInfo.getRequestedCis());
        resultMap.put(cisInfo.getRequestedCis(), new CisInfoResponse(cisInfo));
    }
}
