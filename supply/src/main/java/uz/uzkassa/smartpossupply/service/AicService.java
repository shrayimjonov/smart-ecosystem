package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.domain.aic.Aic;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.dto.aic.AicDTO;
import uz.uzkassa.smartpossupply.dto.aic.AicDetailsDTO;
import uz.uzkassa.smartpossupply.dto.aic.AicListDTO;
import uz.uzkassa.smartpossupply.dto.aic.AicStockInDetailsDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponseResult;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.AicDocumentRequest;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.AicDocumentResponse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.AicRequest;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.AicResponse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.xtrace.AicDisband;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.xtrace.AicDocumentXtraceRequest;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.xtrace.AicRegister;
import uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.privateinfo.CisPrivateInfoResponse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.privateinfo.CisPrivateInfoResult;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.category.CategoryEnvironmentDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonAicDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductFullDetailDTO;
import uz.uzkassa.smartpossupply.enums.AicStatus;
import uz.uzkassa.smartpossupply.enums.CisType;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;
import uz.uzkassa.smartpossupply.repository.AicRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AicService extends BaseService {
    AicRepository aicRepository;
    ProductRepository productRepository;
    AslBelgiClient aslBelgiClient;

    @Transactional
    public Long create(AicDTO aicDTO) {
        Product product = productRepository.findById(aicDTO.getProductId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "id", aicDTO.getProductId()));
        Map<String, CodeType> cises = new HashMap<>();
        if (!CollectionUtils.isEmpty(aicDTO.getAggregationCodes())) {
            aicDTO.getAggregationCodes().forEach(aggregation -> cises.put(aggregation, CodeType.AGGREGATION));
        }
        if (!CollectionUtils.isEmpty(aicDTO.getMarkCodes())) {
            aicDTO.getMarkCodes().forEach(mark -> cises.put(mark, CodeType.MARK));
        }
        if (!CollectionUtils.isEmpty(aicDTO.getAggregationBlockCodes())) {
            aicDTO.getAggregationBlockCodes().forEach(block -> cises.put(block, CodeType.BLOCK));
        }

        if (CollectionUtils.isEmpty(cises)) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.FIELD_IS_EMPTY, new Object[]{"cises"})).get();
        }
        Set<String> allByCodesIn = aicRepository.findAllByCodesIn(cises.keySet());
        if (!CollectionUtils.isEmpty(allByCodesIn)) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.CODE_ALREADY_USED_ERROR, new Object[]{allByCodesIn}), "Эти коды уже исползовано " + allByCodesIn).get();
        }

        Company company = getCurrentCompany();
        String tnved = Optional.ofNullable(aicDTO.getTnVed()).orElse(product.getTnved());
        Aic aic = new Aic();
        aic.setCompanyId(company.getId());
        aic.setProductId(product.getId());
        aic.setProduct(product);
        aic.setReportTime(LocalDateTime.now());
        aic.setStatus(AicStatus.FORMED);
        aic.setQty(aicDTO.getQty());
        aic.setCodes(cises);
        aic.setMarkQty(aicDTO.getMarkQty());
        aic.setTnvedAddUnits(aicDTO.getTnvedAddUnits());
        aic.setTnvedAddUnitsCount(aicDTO.getTnvedAddUnitsCount());
        if (ProductGroup.Alcohol == company.getProductGroup() || ProductGroup.Appliances == company.getProductGroup()) {
            String generatedAic = aslBelgiClient.generateAic(getXTraceInfo().getApiKey());
            if (generatedAic != null) {
                aic.setReportId(generatedAic);
            } else {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.AIC_NOT_FORMED), "АИК не сформирован").get();
            }
        } else {
            AicRequest aicRequest = new AicRequest();
            aicRequest.setInn(company.getTin());
            aicRequest.setPg(company.getProductGroup().getExtension());
            aicRequest.setProductName(product.getName());
            aicRequest.setGtin(MarkUtils.GTIN_FORMAT.format(Long.valueOf(product.getBarcode())));
            aicRequest.setCises(new HashSet<>(cises.keySet()));
            aicRequest.setTnved(tnved);
            aicRequest.setCisesCount(aicDTO.getQty());
            if (aicDTO.getTnvedAddUnits() != null) {
                aicRequest.setTnvedAddUnits(aicDTO.getTnvedAddUnits().toString());
            }
            aicRequest.setTnvedAddUnitsCount(aicDTO.getTnvedAddUnitsCount());
            AicResponse aicEnrichmentResponse = aslBelgiClient.aicEnrichment(getCompanyAggregationInfo().getAslBelgiToken(), aicRequest);
            if (aicEnrichmentResponse.getAic() != null) {
                aic.setReportId(aicEnrichmentResponse.getAic());
                try {
                    aic.setReportContent(Base64.getEncoder().encodeToString(objectMapper.writeValueAsBytes(aicEnrichmentResponse)));
                } catch (Exception e) {
                    log.error("Aic enrichment response serialization error: {}", e.getMessage(), e);
                }
            } else {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.AIC_NOT_FORMED), "АИК не сформирован").get();
            }
        }
        aicRepository.save(aic);
        if (!tnved.equals(product.getTnved())) {
            product.setTnved(tnved);
            productRepository.save(product);
        }
        return aic.getId();
    }

    public Page<AicListDTO> getList(BaseFilter filter) {
        ResultList<Aic> resultList = aicRepository.getResultList(filter);
        List<AicListDTO> result = resultList.getList().stream().map(aic -> {
            AicListDTO aicListDTO = new AicListDTO();
            aicListDTO.setId(aic.getId());
            aicListDTO.setAic(aic.getReportId());
            aicListDTO.setCreatedAt(aic.getReportTime());
            aicListDTO.setStatus(aic.getStatus());
            aicListDTO.setCreator(aic.getCreator().toCommonDTO());
            aicListDTO.setProduct(aic.getProduct().toProductCommonDTO());
            aicListDTO.setProductType(aic.getProduct().getProductType());
            aicListDTO.setQty(aic.getQty());
            return aicListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public AicDetailsDTO get(Long id) {
        Aic aic = aicRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(Aic.class.getSimpleName(), "id", id));
        AicDetailsDTO detailsDTO = new AicDetailsDTO();
        try {
            if (aic.getReportContent() != null) {
                detailsDTO.setForSign(objectMapper.readValue(Base64.getDecoder().decode(aic.getReportContent()), AicResponse.class));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        detailsDTO.setId(aic.getId());
        detailsDTO.setAic(aic.getReportId());
        detailsDTO.setCreatedAt(aic.getReportTime());
        detailsDTO.setStatus(aic.getStatus());
        detailsDTO.setCreator(aic.getCreator().toCommonDTO());
        if (aic.getProduct() != null) {
            ProductCommonAicDTO productCommonAicDTO = new ProductCommonAicDTO();
            productCommonAicDTO.setId(aic.getProduct().getId());
            productCommonAicDTO.setTnved(aic.getProduct().getTnved());
            productCommonAicDTO.setBarcode(MarkUtils.GTIN_FORMAT.format(Long.valueOf(aic.getProduct().getBarcode())));
            productCommonAicDTO.setName(aic.getProduct().getName());
            detailsDTO.setProduct(productCommonAicDTO);
            detailsDTO.setProductType(aic.getProduct().getProductType());
            detailsDTO.setAdditionalUnit(aic.getProduct().getAdditionalUnit());
        }
        detailsDTO.setQty(aic.getQty());
        detailsDTO.setMarkQty(aic.getMarkQty());
        detailsDTO.setTnvedAddUnits(aic.getTnvedAddUnits());
        detailsDTO.setTnvedAddUnitsCount(aic.getTnvedAddUnitsCount());
        Hibernate.initialize(aic.getCodes());

        for (Map.Entry<String, CodeType> entry : aic.getCodes().entrySet()) {
            if (CodeType.AGGREGATION.equals(entry.getValue())) {
                detailsDTO.getAggregations().add(entry.getKey());
            } else if (CodeType.BLOCK.equals(entry.getValue())) {
                detailsDTO.getBlocks().add(entry.getKey());
            } else if (CodeType.MARK.equals(entry.getValue())) {
                detailsDTO.getMarks().add(entry.getKey());
            }
        }
        return detailsDTO;
    }

    public List<CommonDTO> lookUp(BaseFilter filter) {
        return aicRepository.lookUp(filter).stream().map(Aic::toCommonDTO).toList();
    }

    @Transactional
    public Long confirmation(AicDocumentRequest request) {
        Company company = getCurrentCompany();
        try {
            String json = new String(Base64.getDecoder().decode(request.getDocument()));
            Object readValue = objectMapper.readValue(json, Object.class);
            if (readValue != null) {
                String reportId = readValue instanceof AicResponse ? (objectMapper.convertValue(readValue, AicResponse.class)).getAic() : (objectMapper.convertValue(readValue, AicRegister.class)).getAggregatedCustomsCode();
                Aic aic = aicRepository.findFirstByReportIdAndDeletedFalse(reportId).orElseThrow(validationService.entityNotFoundExceptionThrow(Aic.class.getSimpleName(), "reportId", reportId));
                if (AicStatus.SENT.equals(aic.getStatus())) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.DOCUMENT_ALREADY_REGISTERED), "Документ уже зарегистрировано").get();
                }
                if (AicStatus.DISBANDED.equals(aic.getStatus())) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.DOCUMENT_ERROR), "Документ расформирован").get();
                }

                AicDocumentResponse aicDocumentResponse = ProductGroup.Alcohol == company.getProductGroup() || ProductGroup.Appliances == company.getProductGroup()
                        ? aslBelgiClient.registerAic(getXTraceInfo().getApiKey(), new AicDocumentXtraceRequest(request.getDocument(), request.getSignature()))
                        : aslBelgiClient.aicConfirmation(validateAslBelgiAndGetAggregationInfo().getAslBelgiToken(), getCurrentCompanyTin(), request);
                if (aicDocumentResponse.getDocumentId() != null) {
                    aic.setDocumentId(aicDocumentResponse.getDocumentId());
                    aic.setReportTime(LocalDateTime.now());
                    aic.setStatus(AicStatus.SENT);
                    aicRepository.save(aic);
                }
                return aic.getId();
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Transactional
    public Long disAggregation(AicDocumentRequest request) {
        Company company = getCurrentCompany();
        try {
            String json = new String(Base64.getDecoder().decode(request.getDocument()));
            Object readValue = objectMapper.readValue(json, Object.class);
            if (readValue != null) {
                String reportId = readValue instanceof AicResponse ? (objectMapper.convertValue(readValue, AicResponse.class)).getAic() : (objectMapper.convertValue(readValue, AicDisband.class)).getCode();
                Aic aic = aicRepository.findFirstByReportIdAndDeletedFalse(reportId).orElseThrow(validationService.entityNotFoundExceptionThrow(Aic.class.getSimpleName(), "reportId", reportId));
                if (AicStatus.DISBANDED.equals(aic.getStatus())) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.DOCUMENT_ERROR), "Документ уже расформирован").get();
                } else if (!AicStatus.SENT.equals(aic.getStatus())) {
                    throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.DOCUMENT_NOT_REGISTERED), "Документ не зарегистрировано").get();
                }

                AicDocumentResponse aicDocumentResponse = ProductGroup.Alcohol == company.getProductGroup() || ProductGroup.Appliances == company.getProductGroup()
                        ? aslBelgiClient.disbandAic(getXTraceInfo().getApiKey(), new AicDocumentXtraceRequest(request.getDocument(), request.getSignature()))
                        : aslBelgiClient.aicDisaggregate(validateAslBelgiAndGetAggregationInfo().getAslBelgiToken(), getCurrentCompanyTin(), request);
                if (aicDocumentResponse.getDocumentId() != null) {
                    aic.setStatus(AicStatus.DISBANDED);
                    aicRepository.save(aic);
                }
                return aic.getId();
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public List<CisInfoResponse> readFromFile(FileType fileType, ProductType productType, MultipartFile file) throws IOException, CsvValidationException {
        List<String> codes = new LinkedList<>();
        if (FileType.CSV.equals(fileType)) {
            CSVParser csvParser = new CSVParserBuilder()
                    .withSeparator(';')
                    .withQuoteChar('\"')
                    .build();
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream()))
                    .withCSVParser(csvParser).build();
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                codes.add(values[0]);
            }
        } else {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            for (int i = 0; i < rowCount; i++) {
                XSSFRow row = sheet.getRow(i);
                if (row != null && row.getCell(0) != null) {
                    if (CellType.STRING.equals(row.getCell(0).getCellType())) {
                        codes.add(row.getCell(0).getStringCellValue());
                    } else if (CellType.NUMERIC.equals(row.getCell(0).getCellType())) {
                        codes.add(String.valueOf(row.getCell(0).getNumericCellValue()));
                    } else {
                        codes.add(row.getCell(0).getRawValue());
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(codes)) {
            BatchValidationDTO validationDTO = new BatchValidationDTO();
            validationDTO.setProductType(productType);
            validationDTO.setAggregationCodes(codes.stream().filter(MarkUtils::isAggregation).collect(Collectors.toSet()));
            validationDTO.setMarkCodes(codes.stream().filter(MarkUtils::isMark).collect(Collectors.toSet()));
            return check(validationDTO);
        }
        throw validationService.badRequestExceptionThrow("Файл пуст").get();
    }

    public List<CisInfoResponse> check(BatchValidationDTO validationDTO) {
        Map<String, CisInfoResponse> resultMap = new LinkedHashMap<>();
        List<String> aggregationCodes = validationDTO.getAggregationCodes();
        List<String> markCodes = validationDTO.getMarkCodes();
        List<String> aggregationBlockCodes = validationDTO.getAggregationBlockCodes();

        if (!CollectionUtils.isEmpty(aggregationCodes)) {
            aggregationCodes.forEach(aggregationCode -> {
                CisInfo cisInfo = new CisInfo();
                cisInfo.setRequestedCis(aggregationCode);
                cisInfo.setOriginalCode(aggregationCode);
                cisInfo.setPackageType(CodeType.AGGREGATION.name());
                resultMap.put(aggregationCode, new CisInfoResponse(cisInfo));
            });

            List<String> alreadyFormedInAic = aicRepository.findAllByCodesIn(aggregationCodes);
            //send to turon
            if (!CollectionUtils.isEmpty(alreadyFormedInAic)) {
                alreadyFormedInAic.forEach(item -> {
                    CisInfoResponse cisInfoResponse = resultMap.get(item);
                    CisInfo cisInfo = cisInfoResponse.getCisInfo();
                    cisInfo.setStatus(TuronMarkStatus.EMITTED);
                    cisInfo.setErrorMessage("Код уже использован");
                    cisInfo.setEntityType(EntityTypeEnum.AIC.name());
                    cisInfoResponse.setCisInfo(cisInfo);

                    resultMap.remove(cisInfo.getOriginalCode());
                    resultMap.put(cisInfo.getOriginalCode(), cisInfoResponse);
                    aggregationCodes.remove(item);
                });
            }
        }
        if (!CollectionUtils.isEmpty(markCodes)) {
            markCodes.forEach(markCode -> {
                CisInfo cisInfo = new CisInfo();
                cisInfo.setRequestedCis(markCode);
                cisInfo.setOriginalCode(markCode);
                cisInfo.setPackageType(CodeType.MARK.name());
                resultMap.put(markCode, new CisInfoResponse(cisInfo));
            });

            List<String> alreadyFormedInAic = aicRepository.findAllByCodesIn(markCodes);
            //send to turon
            if (!CollectionUtils.isEmpty(alreadyFormedInAic)) {
                alreadyFormedInAic.forEach(item -> {
                    CisInfoResponse cisInfoResponse = resultMap.get(item);
                    CisInfo cisInfo = cisInfoResponse.getCisInfo();
                    cisInfo.setStatus(TuronMarkStatus.EMITTED);
                    cisInfo.setErrorMessage("Код уже использован");
                    cisInfo.setEntityType(EntityTypeEnum.AIC.name());
                    cisInfoResponse.setCisInfo(cisInfo);

                    resultMap.remove(cisInfo.getOriginalCode());
                    resultMap.put(cisInfo.getOriginalCode(), cisInfoResponse);
                    markCodes.remove(item);
                });
            }
        }
        if (!CollectionUtils.isEmpty(aggregationBlockCodes)) {
            aggregationBlockCodes.forEach(aggregationBlockCode -> {
                CisInfo cisInfo = new CisInfo();
                cisInfo.setRequestedCis(aggregationBlockCode);
                cisInfo.setOriginalCode(aggregationBlockCode);
                cisInfo.setPackageType(CodeType.BLOCK.name());
                resultMap.put(aggregationBlockCode, new CisInfoResponse(cisInfo));
            });

            List<String> alreadyFormedInAic = aicRepository.findAllByCodesIn(aggregationBlockCodes);
            //send to turon
            if (!CollectionUtils.isEmpty(alreadyFormedInAic)) {
                alreadyFormedInAic.forEach(item -> {
                    CisInfoResponse cisInfoResponse = resultMap.get(item);
                    CisInfo cisInfo = cisInfoResponse.getCisInfo();
                    cisInfo.setStatus(TuronMarkStatus.EMITTED);
                    cisInfo.setErrorMessage("Код уже использован");
                    cisInfo.setEntityType(EntityTypeEnum.AIC.name());
                    cisInfoResponse.setCisInfo(cisInfo);

                    resultMap.remove(cisInfo.getOriginalCode());
                    resultMap.put(cisInfo.getOriginalCode(), cisInfoResponse);
                    aggregationBlockCodes.remove(item);
                });
            }
        }

        if (!CollectionUtils.isEmpty(aggregationCodes) || !CollectionUtils.isEmpty(markCodes) || !CollectionUtils.isEmpty(aggregationBlockCodes)) {
            CompanySettings xtraceSettings = companySettingsRepository.findFirstByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.XTRACE_ENABLED).orElseThrow(validationService.entityNotFoundExceptionThrow(CompanySettings.class.getSimpleName(), "companyId", getCurrentCompanyId()));
            String token = xtraceSettings.isEnabled() ? getXTraceInfo().getApiKey() : validateAslBelgiAndGetAggregationInfo().getAslBelgiToken();
            if (xtraceSettings.isEnabled()) {
                try {
                    CisInfoResponseResult cisInfoResponseResult = aslBelgiClient.checkPrivateInfo(token, aggregationCodes, markCodes, aggregationBlockCodes, validationDTO.getProductType());
                    if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
                        for (CisInfoResponse cisInfoResponse : cisInfoResponseResult.getCisInfoResponses()) {
                            resultMap.remove(cisInfoResponse.getCisInfo().getOriginalCode());
                            resultMap.put(cisInfoResponse.getCisInfo().getOriginalCode(), cisInfoResponse);
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                //Check Cis Info
                try {
                    CisInfoResponseResult cisInfoResponseResult = aslBelgiClient.checkCisesInfo(token, aggregationCodes, markCodes, aggregationBlockCodes, validationDTO.getProductType());
                    if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
                        for (CisInfoResponse cisInfoResponse : cisInfoResponseResult.getCisInfoResponses()) {
                            CisInfo cisInfo = cisInfoResponse.getCisInfo();
                            if (cisInfo.getPackageType() != null) {
                                if (Arrays.asList(CisType.LEVEL2.getCode(), CisType.LEVEL3.getCode()).contains(cisInfo.getPackageType())) {
                                    cisInfo.setPackageType(CodeType.AGGREGATION.name());
                                    setQty(cisInfoResponse, cisInfo);
                                } else if (CisType.LEVEL1.equals(CisType.valueOf(cisInfo.getPackageType()))) {
                                    cisInfo.setPackageType(CodeType.BLOCK.name());
                                    setQty(cisInfoResponse, cisInfo);
                                } else if (CisType.UNIT.equals(CisType.valueOf(cisInfo.getPackageType()))) {
                                    cisInfo.setPackageType(CodeType.MARK.name());
                                    cisInfo.setQty(BigDecimal.ONE);
                                }
                            }
                            resultMap.remove(cisInfo.getRequestedCis());

                            resultMap.put(cisInfo.getRequestedCis(), cisInfoResponse);

                            if (!CollectionUtils.isEmpty(aggregationCodes)) {
                                aggregationCodes.remove(cisInfo.getRequestedCis());
                            }
                            if (!CollectionUtils.isEmpty(markCodes)) {
                                markCodes.remove(cisInfo.getRequestedCis());
                            }
                            if (!CollectionUtils.isEmpty(aggregationBlockCodes)) {
                                aggregationBlockCodes.remove(cisInfo.getRequestedCis());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                //Check Public Info
                if (!CollectionUtils.isEmpty(aggregationCodes) || !CollectionUtils.isEmpty(markCodes) || !CollectionUtils.isEmpty(aggregationBlockCodes)) {
                    try {
                        CisInfoResponseResult cisInfoResponseResult = aslBelgiClient.checkAggregatedPublicList(token, aggregationCodes, markCodes, aggregationBlockCodes, validationDTO.getProductType());
                        if (cisInfoResponseResult != null && !CollectionUtils.isEmpty(cisInfoResponseResult.getCisInfoResponses())) {
                            for (CisInfoResponse cisInfoResponse : cisInfoResponseResult.getCisInfoResponses()) {
                                resultMap.remove(cisInfoResponse.getCisInfo().getOriginalCode());
                                resultMap.put(cisInfoResponse.getCisInfo().getOriginalCode(), cisInfoResponse);
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }

        return Arrays.asList(resultMap.values().toArray(new CisInfoResponse[0]));
    }

    public ProductFullDetailDTO getByBranchOrClone(String productBarcode, Long branchId) {
        String finalProductBarcode;
        if (productBarcode.length() > 13) {
            finalProductBarcode = productBarcode.substring(1, 14);
        } else {
            finalProductBarcode = productBarcode;
        }
        Product productByBarcode = productRepository.findFirstByBarcodeAndBranchIdAndDeletedIsFalse(productBarcode, branchId).orElseGet(() -> {
            Product productByCompany = productRepository.findFirstByBarcodeAndCompanyIdAndDeletedIsFalse(finalProductBarcode, getCurrentCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Product.class.getSimpleName(), "barcode", finalProductBarcode));
            productByCompany.setBranchId(branchId);
            productByCompany.setPrice(BigDecimal.ZERO);
            branchRepository.findById(branchId).ifPresent(productByCompany::setBranch);
            return productByCompany;
        });
        return getProductDetailDTO(productByBarcode);
    }

    private ProductFullDetailDTO getProductDetailDTO(Product product) {
        ProductFullDetailDTO productFullDetailDTO = new ProductFullDetailDTO();
        productFullDetailDTO.setName(product.getName());
        productFullDetailDTO.setShortName(product.getShortName());
        productFullDetailDTO.setBarcode(product.getBarcode());
        productFullDetailDTO.setProductType(product.getProductType());
        productFullDetailDTO.setCategories(product.getCategories().stream().map(category -> {
            CategoryEnvironmentDTO categoryEnvironmentDTO = new CategoryEnvironmentDTO();
            categoryEnvironmentDTO.setId(category.getId());
            categoryEnvironmentDTO.setName(category.getName());
            categoryEnvironmentDTO.setEnvironmentType(category.getEnvironmentType());
            return categoryEnvironmentDTO;
        }).toList());
        productFullDetailDTO.setBranch(product.getBranch().toCommonDTO());
        productFullDetailDTO.setVat(product.getVat());
        productFullDetailDTO.setUnit(product.getUnit().toDTO());
        productFullDetailDTO.setPrice(product.getPrice());
        productFullDetailDTO.setFavourite(product.isFavourite());
        productFullDetailDTO.setHasMark(product.isHasMark());
        productFullDetailDTO.setVatBarcode(product.getVatBarcode());
        productFullDetailDTO.setCatalogName(product.getCatalogName());
        productFullDetailDTO.setPackageName(product.getPackageName());
        productFullDetailDTO.setPackageCode(product.getPackageCode());
        productFullDetailDTO.setQtyInPackage(product.getQtyInPackage());
        productFullDetailDTO.setQtyInBox(product.getQtyInBox());
        productFullDetailDTO.setQtyInPallet(product.getQtyInPallet());
        productFullDetailDTO.setCapacity(product.getCapacity());
        productFullDetailDTO.setTnved(product.getTnved());
        productFullDetailDTO.setId(product.getId());
        productFullDetailDTO.setBlockProducts(product.getBlockProducts());
        return productFullDetailDTO;
    }

    @Transactional
    public CisPrivateInfoResponse getDetailOrCreateForStockIn(String aicCode, Long branchId) {
        Optional<Aic> aic = aicRepository.findFirstByReportIdAndDeletedFalse(aicCode);
        AicStockInDetailsDTO aicStockInDetailsDTO = new AicStockInDetailsDTO();
        CisPrivateInfoResponse privateInfoResponse = null;
        if (aic.isPresent()) {
            //aic found from db
            aicStockInDetailsDTO.setId(aic.get().getId());
            aicStockInDetailsDTO.setAicDocumentId(aic.get().getDocumentId());
            aicStockInDetailsDTO.setStatus(aic.get().getStatus());
            aicStockInDetailsDTO.setAicReportId(aic.get().getReportId());
            aicStockInDetailsDTO.setQty(aic.get().getQty());
            aicStockInDetailsDTO.setProduct(getByBranchOrClone(aic.get().getProduct().getBarcode(), branchId));
            for (Map.Entry<String, CodeType> codeItem : aic.get().getCodes().entrySet()) {
                if (CodeType.AGGREGATION.equals(codeItem.getValue())) {
                    aicStockInDetailsDTO.getAggregations().add(codeItem.getKey());
                } else if (CodeType.BLOCK.equals(codeItem.getValue())) {
                    aicStockInDetailsDTO.getBlocks().add(codeItem.getKey());
                } else if (CodeType.MARK.equals(codeItem.getValue())) {
                    aicStockInDetailsDTO.getMarks().add(codeItem.getKey());
                }
            }
        } else {
            // aic not found from db
            /*AicCodeHistory aicCodeHistory = aslBelgiClient.getCiseHistory(getCompanyAggregationInfo().getAslBelgiToken(), getCurrentCompanyTin(), aicCode);
            if (aicCodeHistory == null) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ENTITY_NOT_FOUND_BY, new Object[]{"Document", aicCode})).get();
            }
            for (AicCodeHistoryItem aicCodeHistoryItem : aicCodeHistory.getHistory()) {
                if (aicCodeHistoryItem.getDocumentType().equals("AIC_AGGREGATION")) {
                    AicDocument aicDocumentContentResponse = aslBelgiClient.documentInfo(getCompanyAggregationInfo().getAslBelgiToken(), getCurrentCompanyTin(), aicCodeHistoryItem.getDocId());
                    aicStockInDetailsDTO.setAicDocumentId(aicCodeHistoryItem.getDocId());
                    aicStockInDetailsDTO.setAicReportId(aicCode);
                    aicStockInDetailsDTO.setQty(aicDocumentContentResponse.getCisesCount());
                    aicStockInDetailsDTO.setProduct(getByBranchOrClone(aicDocumentContentResponse.getGtin(), branchId));

                    CodeType codeType = CodeType.AGGREGATION;
                    Optional<String> cisesItem = aicDocumentContentResponse.getCises().stream().findFirst();
                    if (cisesItem.isPresent()) {
                        CisInfoResponse[] cisInfoResponses = aslBelgiClient.cisesInfo(getCurrentCompanyTin(), getCompanyAggregationInfo().getAslBelgiToken(), Collections.singleton(cisesItem.get()));
                        if (cisInfoResponses.length > 0) {
                            CisInfo cisInfo = cisInfoResponses[0].getCisInfo();
                            if (CisType.LEVEL1.equals(CisType.valueOf(cisInfo.getPackageType()))) {
                                aicStockInDetailsDTO.getBlocks().addAll(aicStockInDetailsDTO.getAggregations());
                                codeType = CodeType.BLOCK;
                            } else if (CisType.UNIT.equals(CisType.valueOf(cisInfo.getPackageType()))) {
                                aicStockInDetailsDTO.getMarks().addAll(aicStockInDetailsDTO.getAggregations());
                                codeType = CodeType.MARK;
                            }
                        }
                    }
                    Optional<Aic> firstByReportId = aicRepository.findFirstByReportIdAndDeletedFalse(aicCode);
                    if (firstByReportId.isEmpty()) {
                        Aic newAic = new Aic();
                        newAic.setStatus(AicStatus.SENT);
                        newAic.setCompanyId(getCurrentCompanyId());
                        newAic.setDocumentId(aicCodeHistoryItem.getDocId());
                        newAic.setReportTime(LocalDateTime.now());
                        newAic.setReportId(aicCode);
                        newAic.setQty(aicDocumentContentResponse.getCisesCount());
                        for (String code : aicDocumentContentResponse.getCises()) {
                            newAic.getCodes().put(code, codeType);
                        }
                        newAic.setProductId(aicStockInDetailsDTO.getProduct().getId());
                        aicRepository.save(newAic);
                    }
                }
            }
            }*/
            privateInfoResponse = aslBelgiClient.getPrivateInfoByCode(aicCode, getXTraceInfo().getApiKey());

            CisPrivateInfoResult rootResult = privateInfoResponse.getResults().getFirst();
            String packageType = rootResult.getPackageData() != null ? rootResult.getPackageData().getPackageType() : null;
            CodeType codeType;
            if ("UNIT".equalsIgnoreCase(packageType)) {
                codeType = CodeType.MARK;
            } else if ("GROUP".equalsIgnoreCase(packageType)) {
                codeType = CodeType.BLOCK;
            } else {
                codeType = CodeType.AGGREGATION;
            }

            Aic newAic = new Aic();
            newAic.setStatus(AicStatus.SENT);
            newAic.setCompanyId(getCurrentCompanyId());
            newAic.setReportTime(LocalDateTime.now());
            newAic.setReportId(aicCode);
            newAic.setQty(rootResult.getPackageData() != null && rootResult.getPackageData().getActuallyPacked() != null ? rootResult.getPackageData().getActuallyPacked().intValue(): 0);
            newAic.setProductId(getByBranchOrClone(rootResult.getProductData().getProductId(), branchId).getId());
            newAic.getCodes().put(rootResult.getCodeData() != null && rootResult.getCodeData().getCode() != null? rootResult.getCodeData().getCode(): aicCode,codeType);
            aicRepository.save(newAic);
            aicStockInDetailsDTO.setId(newAic.getId());
        }

        return privateInfoResponse;
    }
    private void setQty(CisInfoResponse cisInfoResponse, CisInfo cisInfo) {
        List<String> children = null;
        if (!CollectionUtils.isEmpty(cisInfoResponse.getCisInfo().getChild())) {
            children = cisInfoResponse.getCisInfo().getChild();
        } else if (!CollectionUtils.isEmpty(cisInfoResponse.getCisInfo().getCodes())) {
            children = new ArrayList<>(cisInfoResponse.getCisInfo().getCodes().keySet());
        }
        if (!CollectionUtils.isEmpty(children)) {
            cisInfo.setQty(BigDecimal.valueOf(children.size()));
        }
    }

/*    public AicStockInDetailsDTO resolveUnitCodesByAic(String aicCode, String token) {
        AicStockInDetailsDTO result = new AicStockInDetailsDTO();
        aslBelgiClient.resolveRecursivelyByPrivateCodes(aicCode, token, result);
        return result;
    }*/

}