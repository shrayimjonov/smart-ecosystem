package uz.uzkassa.silen.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.domain.BatchAggregation;
import uz.uzkassa.silen.domain.ExportReport;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.filter.BatchFilter;
import uz.uzkassa.silen.dto.marking.BatchAggregationDTO;
import uz.uzkassa.silen.dto.marking.BatchAggregationListDTO;
import uz.uzkassa.silen.dto.marking.BatchAggregationResponseDTO;
import uz.uzkassa.silen.dto.mq.ExportReportPayload;
import uz.uzkassa.silen.dto.mq.Payload;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.onec.CheckBatchStatusDTO;
import uz.uzkassa.silen.repository.pg.AggregationRepository;
import uz.uzkassa.silen.repository.pg.BatchAggregationRepository;
import uz.uzkassa.silen.repository.pg.ExportReportRepository;
import uz.uzkassa.silen.utils.Utils;

import java.time.LocalDateTime;
import java.util.*;

import static uz.uzkassa.silen.exceptions.ErrorConstants.TURON_AGGREGATION_NOT_FOUND;

/**
 * Service Implementation for managing {@link BatchAggregation}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService extends BaseService {
    private final BatchAggregationRepository batchAggregationRepository;
    private final AggregationRepository aggregationRepository;
    private final AsilBelgisiClient asilBelgisiClient;
    private final ExportReportRepository exportReportRepository;

    public BatchAggregationResponseDTO save(BatchAggregationDTO batchAggregationDTO) {
        final Organization organization = getCurrentOrganization();
        if (StringUtils.isEmpty(batchAggregationDTO.getGcp())) {
            throw badRequest("Отсуствует код организации").get();
        }
        if (!organization.getGcp().contains(batchAggregationDTO.getGcp())) {
            throw badRequest("Невалидный GCP для данной организации").get();
        }
        batchAggregationRepository.findFirstByOrganizationIdAndStatusOrderByCreatedDateDesc(organization.getId(), BatchAggregationStatus.PENDING)
                .ifPresent(batchAggregation -> {
                    if (batchAggregation.getCreatedDate().isAfter(LocalDateTime.now().minusMinutes(10))) {
                        throw badRequest("Действие выполнить невозможн. Ожидайте генерации предыдущего файла.").get();
                    } else {
                        batchAggregation.setStatus(BatchAggregationStatus.INVALID);
                        batchAggregationRepository.save(batchAggregation);
                    }
                });
        BatchAggregation batchAggregation = new BatchAggregation();
        batchAggregation.setOrganizationId(organization.getId());
        batchAggregation.setQuantity(batchAggregationDTO.getQuantity());
        batchAggregation.setUnit(batchAggregationDTO.getUnit().getCode());
        batchAggregation.setDescription(batchAggregationDTO.getDescription());
        batchAggregation.setGcp(batchAggregationDTO.getGcp());
        batchAggregation.setStatus(BatchAggregationStatus.PENDING);
        batchAggregation.setExportToFile(batchAggregationDTO.isExportToFile());

        Integer maxIntNumber = batchAggregationRepository.getMaxIntNumber(organization.getId());
        maxIntNumber = maxIntNumber == null ? 1 : maxIntNumber + 1;
        batchAggregation.setIntNumber(maxIntNumber);
        batchAggregation.setNumber("BATCH-" + maxIntNumber);

        batchAggregationRepository.save(batchAggregation);
        rabbitMqProducer.sendBatchAggregation(new Payload(batchAggregation.getId()));
        return new BatchAggregationResponseDTO(batchAggregation.getId());
    }

    public Page<BatchAggregationListDTO> findAll(BatchFilter filter) {
        return batchAggregationRepository.findAllByFilter(filter)
                .map(BatchAggregationListDTO::new);
    }

    public BatchAggregationListDTO findOne(String id) {
        return batchAggregationRepository.findById(id)
                .map(BatchAggregation::toDto)
                .orElseThrow(notFound("Batch Aggregation not found " + id));
    }

    public void batchAggregationRecursion(final Payload payload) {
        log.info("batch aggregation recursion {}, {}", payload.getId(), payload.getLongId());
        BatchAggregation batchAggregation = batchAggregationRepository.findById(payload.getId()).orElseThrow(notFound("Batch Aggregation not found " + payload.getId()));
        final Organization organization = batchAggregation.getOrganization();
        final String organizationGcp = batchAggregation.getGcp();

        int quantity = batchAggregation.getQuantity();
        do {
            Map<String, Aggregation> aggregationCodes = getResult(Math.min(quantity, AsilBelgisiClient.cisInfoLimit), organizationGcp, organization, batchAggregation);
            aggregationRepository.saveAll(aggregationCodes.values());
            quantity -= aggregationCodes.size();
            log.info("batch aggregation generated {}, {}", aggregationCodes.size(), payload.getId());
        } while (quantity > 0);

        batchAggregation.setStatus(BatchAggregationStatus.COMPLETED);
        batchAggregationRepository.save(batchAggregation);

        if (batchAggregation.getExportToFile()) {
            AggregationFilter filter = new AggregationFilter();
            filter.setOrganizationId(batchAggregation.getOrganizationId());
            filter.setBatchId(batchAggregation.getId());
            filter.setUnit(PackageType.fromCode(batchAggregation.getUnit()));
            filter.setOrderBy("lastModifiedDate");
            filter.setSortOrder(SortTypeEnum.desc);
            filter.setWithChild(false);
            filter.setCodeLength(CodeLength.SHORT);

            ExportReport exportReport = new ExportReport();
            exportReport.setType(ExportReportType.BATCH_JSON_CONTENT);
            exportReport.setFileType(FileType.JSON);
            exportReport.setFileType(FileType.JSON);
            exportReport.setName(batchAggregation.getNumber() + FileType.JSON.getExtension());
            exportReport.setOrganizationId(batchAggregation.getOrganizationId());
            exportReport.setBatchId(batchAggregation.getId());
            try {
                exportReport.setFilters(objectMapper.writeValueAsString(filter));
            } catch (JsonProcessingException ignored) {
            }
            exportReportRepository.save(exportReport);
            final ExportReportPayload payloadExport = new ExportReportPayload(filter, exportReport.getId());
            afterCommitExecutor.execute(() -> rabbitMqProducer.sendExportReport(payloadExport));
        }
    }

    public Map<String, Aggregation> getResult(int quantity, final String organizationGcp, final Organization organization, final BatchAggregation batchAggregation) {
        Map<String, Aggregation> aggregationCodes = new HashMap<>();
        int generated = 0;
        while (generated < quantity) {
            int maxIntNumber = Math.toIntExact(aggregationRepository.nextVal("org_seq_" + organizationGcp));
            Aggregation aggregation = new Aggregation();
            aggregation.setOrganizationId(organization.getId());
            aggregation.setBatchId(batchAggregation.getId());
            aggregation.setLogin(batchAggregation.getCreatedBy());
            aggregation.setSsccCode("(00)");
            aggregation.setUnit(batchAggregation.getUnit());
            aggregation.setCompanyPrefix(organizationGcp);
            aggregation.setIntNumber(maxIntNumber);
            aggregation.setGcp(batchAggregation.getGcp());
            aggregation.setSerialNumber(Utils.generateSerialNumber(maxIntNumber));
            aggregation.setStatus(AggregationStatus.DRAFT);
            String tempNumber = StringUtils.join(aggregation.getUnit(), aggregation.getCompanyPrefix(), aggregation.getSerialNumber());

            String code = StringUtils.join(tempNumber, Utils.generateControlNumber(tempNumber));
            aggregation.setCode("00" + code);
            aggregation.setPrintCode(StringUtils.join(aggregation.getSsccCode(), code));
            aggregationCodes.put(aggregation.getCode(), aggregation);
            generated++;
        }

        try {
            final String currentOrganizationAsilBelgiToken = getCurrentOrganizationAsilBelgiToken(organization.getTin());
            final Set<String> codes = aggregationCodes.keySet();
            Map<String, Map<String, List>> result = asilBelgisiClient.aggregatedList(currentOrganizationAsilBelgiToken, organization.getProductGroup(), codes);
            log.info("checking result {}", result.toString());
            for (String turonCode : result.keySet()) {
                aggregationCodes.remove(turonCode);
            }
            return aggregationCodes;
        } catch (BadRequestException e) {
            log.error("Generating batch KA error {}", e.getMessage());
            if (e.getErrorCode() == TURON_AGGREGATION_NOT_FOUND) {
                return aggregationCodes;
            }
        }
        return aggregationCodes;
    }

    public Long getStats(BatchFilter batchFilter) {
        return batchAggregationRepository.getStats(batchFilter);
    }

    public CheckBatchStatusDTO checkStatus(String id) {
        BatchAggregation batchAggregation = batchAggregationRepository.findById(id).orElseThrow(notFound());
        CheckBatchStatusDTO checkBatchStatusDTO = new CheckBatchStatusDTO();
        checkBatchStatusDTO.setId(id);
        checkBatchStatusDTO.setStatus(batchAggregation.getStatus());
        if (BatchAggregationStatus.COMPLETED.equals(batchAggregation.getStatus())) {
            Optional<ExportReport> file = exportReportRepository.findFirstByOrganizationIdAndBatchIdOrderByCreatedDateDesc(getCurrentOrganizationId(), id);
            file.ifPresent(exportReport -> {
                checkBatchStatusDTO.setFileStatus(exportReport.getStatus());
                if (ReportStatus.COMPLETED.equals(exportReport.getStatus())) {
                    checkBatchStatusDTO.setFileId(exportReport.getId());
                }
            });
        }
        return checkBatchStatusDTO;
    }
}
