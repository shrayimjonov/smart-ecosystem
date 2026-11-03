package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.PartyAggregation;
import uz.uzkassa.silen.domain.mongo.TraceUtilisationRequest;
import uz.uzkassa.silen.dto.UtilisationDTO;
import uz.uzkassa.silen.dto.marking.CheckingHashInfo;
import uz.uzkassa.silen.enumeration.ProductGroup;
import uz.uzkassa.silen.enumeration.UsageType;
import uz.uzkassa.silen.integration.TuronClient;
import uz.uzkassa.silen.integration.turon.UtilisationRequest;
import uz.uzkassa.silen.integration.turon.UtilisationResponse;
import uz.uzkassa.silen.repository.mongo.TraceUtilisationRepository;
import uz.uzkassa.silen.utils.DateUtils;
import uz.uzkassa.silen.utils.ServerUtils;
import uz.uzkassa.silen.utils.Utils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UtilisationService extends BaseService implements Serializable {
    TuronClient turonClient;
    TraceUtilisationRepository traceUtilisationRepository;
    private final View error;

    /**
     * for {@link ProductGroup#Alcohol}
     *
     * @param codes
     * @param organization
     * @param productionDateTime
     * @return
     */
    public UtilisationResponse create(final Set<String> codes, final Organization organization, LocalDateTime productionDateTime) {
        final UtilisationRequest utilisationRequest = new UtilisationRequest();
        utilisationRequest.setSntins(codes);
        utilisationRequest.setUsageType(UsageType.USED_FOR_PRODUCTION);
        utilisationRequest.setProductionDate(productionDateTime.minusHours(6).format(DateUtils.ISO_INSTANT));
        utilisationRequest.setProductionLineId(organization.getId());
        return traceUtilisationResponse(organization.getTuronToken(), organization.getProductGroup().getExtension(), organization.getOmsId(), utilisationRequest);
    }

    /**
     * for {@link ProductGroup#Pharma}
     *
     * @param codes
     * @param organization
     * @param productionDateTime
     * @param expiredDateTime
     * @param serialNumber
     * @return
     */
    public UtilisationResponse create(final Set<String> codes, final Organization organization, LocalDateTime productionDateTime, LocalDateTime expiredDateTime, String serialNumber) {

        final UtilisationRequest utilisationRequest = new UtilisationRequest();
        utilisationRequest.setSntins(codes);
        utilisationRequest.setUsageType(UsageType.USED_FOR_PRODUCTION);
        utilisationRequest.setProductionLineId(organization.getId());
        utilisationRequest.setProductionDate(productionDateTime.minusHours(6).format(DateUtils.ISO_INSTANT));
        if (serialNumber != null) utilisationRequest.setSeriesNumber(ServerUtils.serialNumberCut(serialNumber));
        LocalDateTime expiredDate = Optional.ofNullable(expiredDateTime).orElse(LocalDateTime.now().plusYears(2));
        utilisationRequest.setExpirationDate(expiredDate.minusHours(6).format(DateUtils.ISO_INSTANT));

        return traceUtilisationResponse(organization.getTuronToken(), organization.getProductGroup().getExtension(), organization.getOmsId(), utilisationRequest);
    }

    public UtilisationResponse create(UtilisationDTO utilisationDTO) {
        Organization organization = organizationRepository.findById(Optional.ofNullable(utilisationDTO.getOrganizationId()).orElse(getCurrentOrganizationId())).orElseThrow(notFound("Организация не найден"));

        Set<String> cisesWithUnicode = utilisationDTO.getCises().stream().map(Utils::fillWithUniCode).collect(Collectors.toSet());
        UtilisationRequest utilisationRequest = new UtilisationRequest();
        utilisationRequest.setSntins(cisesWithUnicode);
        utilisationRequest.setUsageType(UsageType.USED_FOR_PRODUCTION);
        utilisationRequest.setProductionDate(utilisationDTO.getProductionDate().minusHours(6).format(DateUtils.ISO_INSTANT));
        utilisationRequest.setProductionLineId(organization.getId());
        if (ProductGroup.Pharma.equals((organization.getProductGroup()))) {
            utilisationRequest.setExpirationDate(utilisationDTO.getExpirationDate().format(DateUtils.ISO_INSTANT));
            utilisationRequest.setSeriesNumber(ServerUtils.serialNumberCut(utilisationDTO.getSerialNumber()));
        }
        return traceUtilisationResponse(organization.getTuronToken(), organization.getProductGroup().getExtension(), organization.getOmsId(), utilisationRequest);
    }

    public UtilisationResponse sendToApplyPartyMarks(final Collection<String> cisesWithUnicode, PartyAggregation partyAggregation, Organization organization) {
        String productionDateTime = LocalDateTime.of(partyAggregation.getProductionDate(), LocalTime.now()).minusHours(6).format(DateUtils.ISO_INSTANT);
        UtilisationRequest utilisationRequest = new UtilisationRequest();
        utilisationRequest.setSntins(new HashSet<>(cisesWithUnicode));
        utilisationRequest.setUsageType(UsageType.USED_FOR_PRODUCTION);
        utilisationRequest.setProductionDate(productionDateTime);
        utilisationRequest.setProductionLineId(partyAggregation.getNumber());
        if (partyAggregation.getSerial() != null) {
            String expirationDate = LocalDateTime.of(partyAggregation.getExpirationDate(), LocalTime.now()).minusHours(6).format(DateUtils.ISO_INSTANT);
            utilisationRequest.setExpirationDate(expirationDate);
            utilisationRequest.setSeriesNumber(ServerUtils.serialNumberCut(partyAggregation.getSerial().getSerialNumber()));
        }
        final String clientToken = organization.getTuronToken();
        final String extension = organization.getProductGroup().getExtension();
        final String omsId = organization.getOmsId();

        TraceUtilisationRequest traceUtilisationRequest = new TraceUtilisationRequest();
        traceUtilisationRequest.setRequest(utilisationRequest);
        traceUtilisationRequest.setExtension(extension);
        traceUtilisationRequest.setClientToken(clientToken);
        traceUtilisationRequest.setOmsId(omsId);
        try {
            UtilisationResponse utilisationResponse = turonClient.apply(clientToken, extension, omsId, utilisationRequest);
            traceUtilisationRequest.setResponse(utilisationResponse);
        } catch (Exception e) {
            traceUtilisationRequest.setResponse(new UtilisationResponse(omsId, null, e.getMessage()));
            log.error("Utilisation error ", e);
        }
        traceUtilisationRepository.save(traceUtilisationRequest);
        return traceUtilisationRequest.getResponse();
    }


    public UtilisationResponse traceUtilisationResponse(String clientToken, String extension, String omsId, UtilisationRequest request) {
        TraceUtilisationRequest traceUtilisationRequest = new TraceUtilisationRequest();
        traceUtilisationRequest.setRequest(request);
        traceUtilisationRequest.setExtension(extension);
        traceUtilisationRequest.setClientToken(clientToken);
        traceUtilisationRequest.setOmsId(omsId);
        try {
            UtilisationResponse utilisationResponse = turonClient.apply(clientToken, extension, omsId, request);
            traceUtilisationRequest.setResponse(utilisationResponse);
            traceUtilisationRepository.save(traceUtilisationRequest);
            return utilisationResponse;
        } catch (Exception e) {
            traceUtilisationRequest.setResponse(new UtilisationResponse(omsId, null, e.getMessage()));
            traceUtilisationRepository.save(traceUtilisationRequest);
            log.error("Utilisation error ", e);
            throw e;
        }
    }

    public UtilisationResponse utiliseByCheckingHashCode(final Collection<String> codes, final Organization organization, CheckingHashInfo checkingHashInfo) {
        final UtilisationRequest utilisationRequest = new UtilisationRequest();
        utilisationRequest.setSntins(new HashSet<>(codes));
        utilisationRequest.setUsageType(UsageType.USED_FOR_PRODUCTION);
        utilisationRequest.setProductionLineId(organization.getId());
        utilisationRequest.setProductionDate(Optional.ofNullable(checkingHashInfo.getProductionDate()).orElse(LocalDateTime.now()).minusHours(6).format(DateUtils.ISO_INSTANT));
        if (StringUtils.isNotEmpty(checkingHashInfo.getSerialNumber())) {
            utilisationRequest.setSeriesNumber(ServerUtils.serialNumberCut(checkingHashInfo.getSerialNumber()));
        }
        LocalDateTime expiredDate = Optional.ofNullable(checkingHashInfo.getExpirationDate()).orElse(LocalDateTime.now().plusYears(2));
        utilisationRequest.setExpirationDate(expiredDate.minusHours(6).format(DateUtils.ISO_INSTANT));

        try {
            return traceUtilisationResponse(organization.getTuronToken(), organization.getProductGroup().getExtension(), organization.getOmsId(), utilisationRequest);
        } catch (Exception e) {
            if (e.getMessage().contains("Specified array of MC has already been registered")) {
                return new UtilisationResponse(null, null);
            } else {
                return new UtilisationResponse(null, null, e.getMessage());
            }
        }
    }
}
