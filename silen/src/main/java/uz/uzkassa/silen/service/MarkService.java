package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.OrderProduct;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.Party;
import uz.uzkassa.silen.domain.mongo.Mark;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.dto.mongo.MarkDTO;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.enumeration.FileType;
import uz.uzkassa.silen.enumeration.MarkStatus;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.repository.mongo.MarkRepository;
import uz.uzkassa.silen.repository.pg.PartyRepository;
import uz.uzkassa.silen.utils.CyrillicLatinConverter;

import java.util.Collections;

/**
 * Service Implementation for managing {@link uz.uzkassa.silen.domain.mongo.Mark}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkService extends BaseService {
    MarkRepository markRepository;
    AsilBelgisiClient asilBelgisiClient;
    PartyRepository partyRepository;
    ExportReportService exportReportService;

    public Page<MarkDTO> findAll(MongoFilter filter) {
        return markRepository.findAllByFilter(filter).map(Mark::toDto);
    }


    /*@Deprecated
    public CisInfoResponse[] cisesInfo(CheckCisesDTO checkCisesDTO) {
        final String organizationId = Optional.ofNullable(checkCisesDTO.getOrganizationId()).orElse(getCurrentOrganizationId());
        final Organization organization = organizationRepository.findById(organizationId).orElseThrow(notFound());
        String currentOrganizationAsilBelgiToken = getCurrentOrganizationAsilBelgiToken(organization.getTin());
        if (StringUtils.isEmpty(currentOrganizationAsilBelgiToken)) {
            throw badRequest("Отсутствует токен организации").get();
        }
        return asilBelgisiClient.cisesInfo(currentOrganizationAsilBelgiToken, checkCisesDTO.getCodes());
    }*/

   /* @Deprecated
    public Set<String> aggregatedList(TrueApiRequest request) {
        final String organizationId = request.getOrganizationId() == null ? getCurrentOrganizationId() : request.getOrganizationId();
        final Set<String> codes = request.getCodes() == null ? Collections.singleton(request.getCode()) : request.getCodes();
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(notFound());
        Map<String, Map<String, List>> result = asilBelgisiClient.aggregatedList(organization.getTrueToken(), organization.getProductGroup(), codes);
        if (result != null && !result.keySet().isEmpty()) {
            for (String key : result.keySet()) {
                Map<String, List> map = result.get(key);
                return map.keySet();
            }
        }
        return null;
    }*/

    public MarkDTO checkMark(MarkDTO markDTO) {
        Organization organization = getCurrentOrganization();
        Mark mark = markRepository.findById(markDTO.getId()).orElseThrow(notFound());

        CisInfoResponse[] result = asilBelgisiClient.cisesInfo(getCurrentOrganizationAsilBelgiToken(organization.getTin()), Collections.singleton(markDTO.getCode()));
        if (result != null && result.length > 0) {
            mark.setCode(markDTO.getCode());
            mark.setTuronStatus(result[0].getCisInfo().getStatus());
            if (MarkStatus.APPLIED.equals(result[0].getCisInfo().getStatus())) {
                mark.setStatus(AggregationStatus.DRAFT);
            }
            markRepository.save(mark);
            return mark.toDto();
        }
        throw new BadRequestException("КМ отсутствует");
    }

    public void generateReport(MongoFilter filter) {
        String attachmentName;
        if (filter.getPartyId() != null) {
            Party party = partyRepository.findById(filter.getPartyId()).orElseThrow(notFound());
            OrderProduct orderProduct = party.getOrderProduct();
            attachmentName = orderProduct.getOrder().getNumber() + "_" +
                    CyrillicLatinConverter.cyrilicToLatin(orderProduct.getProduct().getShortName()) + "_" +
                    CyrillicLatinConverter.cyrilicToLatin(party.getDescription());
        } else {
            attachmentName = "Mark_Codes";
        }
        ExportReportType exportReportType = FileType.CSV.equals(filter.getFileType()) ? ExportReportType.CSV_MARK_KM : ExportReportType.EXCEL_MARK_KM;
        exportReportService.export(filter, exportReportType, filter.getFileType(), attachmentName);
    }
}
