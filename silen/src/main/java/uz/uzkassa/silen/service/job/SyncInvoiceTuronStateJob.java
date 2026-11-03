package uz.uzkassa.silen.service.job;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.ListInvoice;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.dto.filter.OrganizationFilter;
import uz.uzkassa.silen.integration.AsilBelgisiClient;
import uz.uzkassa.silen.integration.asilbelgi.dto.DocList;
import uz.uzkassa.silen.integration.asilbelgi.dto.DocListItem;
import uz.uzkassa.silen.repository.pg.ListInvoiceRepository;
import uz.uzkassa.silen.repository.pg.OrganizationRepository;
import uz.uzkassa.silen.service.BaseService;
import uz.uzkassa.silen.service.TelegramBotApi;
import uz.uzkassa.silen.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
@Service
public class SyncInvoiceTuronStateJob extends BaseService {

    private final OrganizationRepository organizationRepository;
    private final ListInvoiceRepository listInvoiceRepository;
    private final AsilBelgisiClient asilBelgisiClient;
    private final TelegramBotApi telegramBotApi;

    @Profile("prod")
    @Scheduled(cron = "0 0 4 * * *")
    public void syncState() {
        final String dateFrom = LocalDateTime.now().minusDays(1).format(DateUtils.ISO_INSTANT);
        log.debug("SyncInvoiceTuronStateJob dateFrom: {}", dateFrom);
        List<Organization> organizations = organizationRepository.findAllByDeletedIsFalseAndActiveIsTrue();
        checkByOrganizations(organizations, dateFrom);
    }

    public void checkByOrganizationFilter(OrganizationFilter organizationFilter, final LocalDateTime LocalDateTime) {
        final String dateFrom = LocalDateTime.format(DateUtils.ISO_INSTANT);
        boolean hasNext;
        int pageNumber = 0;
        do {
            organizationFilter.setPage(pageNumber++);
            Page<Organization> allByFilter = organizationRepository.findAllByFilter(organizationFilter);
            checkByOrganizations(allByFilter.getContent(), dateFrom);
            hasNext = allByFilter.hasNext();
        } while (hasNext);
    }

    private void checkByOrganizations(final Collection<Organization> organizations, final String dateFrom) {

        for (Organization organization : organizations) {
            try {
                final  String trueToken = getCurrentOrganizationAsilBelgiTokenOrNul(organization.getTin());
                if (StringUtils.isEmpty(trueToken)) {
                    log.error("Organization {} true token is null", organization.getName());
                    continue;
                }
                DocList docList = asilBelgisiClient.getDocList(trueToken, dateFrom);

                if (docList == null) {
                    log.error("Organization {} Doc list is empty or null", organization.getName());
                    continue;
                }
                List<DocListItem> results = docList.getResults();
                if (CollectionUtils.isEmpty(docList.getResults())) {
                    log.error("Organization {} Doc list is empty or null", organization.getName());
                    continue;
                }
                Map<String, DocListItem> collect = results.stream().collect(Collectors.toMap(DocListItem::getInvoiceNumber, docListItem -> docListItem));
                List<ListInvoice> allByFacturaIdIn = listInvoiceRepository.findAllByFacturaIdIn(collect.keySet());
                for (ListInvoice item : allByFacturaIdIn) {
                    if (collect.containsKey(item.getFacturaId())) {
                        DocListItem docListItem = collect.get(item.getFacturaId());
                        item.setTuronStatus(docListItem.getStatus());
                        item.setTuronDocId(docListItem.getNumber());
                        listInvoiceRepository.save(item);
                    }
                }

                telegramBotApi.sendMessageToAdminChannel(localize("sync.invoices", dateFrom, organization.getTin(), results.size()));
            } catch (Exception e) {
                log.error("Organization {} token empty or null", organization.getName());
                log.error(e.getMessage());
            }
        }
    }

}
