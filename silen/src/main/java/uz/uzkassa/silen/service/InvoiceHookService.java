package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Invoice;
import uz.uzkassa.silen.domain.ListInvoice;
import uz.uzkassa.silen.dto.callback.InvoiceCallback;
import uz.uzkassa.silen.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.silen.dto.eimzo.SignatureData;
import uz.uzkassa.silen.enumeration.FacturaType;
import uz.uzkassa.silen.enumeration.Status;
import uz.uzkassa.silen.integration.EimzoClient;
import uz.uzkassa.silen.integration.FacturaClient;
import uz.uzkassa.silen.repository.pg.ListInvoiceRepository;

import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceHookService extends BaseService {
    private final CustomerService customerService;
    private final ListInvoiceRepository listInvoiceRepository;
    private final FacturaClient facturaClient;
    private final TelegramBotApi telegramBotApi;
    private final EimzoClient eimzoClient;

    private static ConcurrentHashMap<String, Status> map = new ConcurrentHashMap<>();

    @Transactional
    public void onUpdate(final InvoiceCallback invoiceCallback) {
        if (invoiceCallback != null) {
            final String facturaId = invoiceCallback.getFacturaId();
            final Status status = invoiceCallback.getState();
            if (StringUtils.isEmpty(facturaId)) {
                telegramBotApi.sendMessageToAdminChannel(StringUtils.join("UPDATE", "\n\n NO FACTURA_ID", "\nJSON\n", invoiceCallback));
                return;
            } else if (map.containsKey(facturaId) && map.get(invoiceCallback.getFacturaId()) == status) {
                return;
            }
            map.put(facturaId, status);
            if (checkOrganization(invoiceCallback.getSellerTin(), invoiceCallback.getBuyerTin())) {
                final ListInvoice invoice = listInvoiceRepository.findOneByFacturaId(facturaId)
                        .orElseGet(ListInvoice::new);
                if (invoice.getId() == null) {
                    invoice.setFacturaId(facturaId);
                    invoice.setFacturaProductId(invoiceCallback.getFacturaProductId());
                    invoice.setNumber(invoiceCallback.getFacturaNo());
                    if (invoiceCallback.getFacturaDate() != null) {
                        invoice.setInvoiceDate(invoiceCallback.getFacturaDate().toLocalDate());
                    }
                    invoice.setSellerTin(invoiceCallback.getSellerTin());
                    invoice.setSellerName(invoiceCallback.getSellerName());
                    invoice.setBuyerTin(invoiceCallback.getBuyerTin());
                    invoice.setBuyerName(invoiceCallback.getBuyerName());
                    invoice.setTotal(invoiceCallback.getPayableTotal());
                    invoice.setNotes(invoiceCallback.getNotes());
                    String sign = "";
                    try {
                        sign = facturaClient.getSellerFacturaSign(invoiceCallback.getSellerTin(), facturaId, status);
                    } catch (Exception e) {
                        String stringBuilder = "\n" + "INVOICE: " + invoiceCallback.getFacturaId() +
                                "\nSELLER: " + invoiceCallback.getSellerTin() +
                                "\nBUYER: " + invoiceCallback.getBuyerTin() +
                                "\nERROR: " + e.getMessage() + "\n";
                        log.error(stringBuilder, e);
                    }
                    if (StringUtils.isEmpty(sign) && StringUtils.isNotEmpty(invoiceCallback.getBuyerTin())) {
                        try {
                            sign = facturaClient.getBuyerFacturaSign(invoiceCallback.getBuyerTin(), facturaId, status);
                        } catch (Exception e) {
                            String stringBuilder = "\n" + "INVOICE: " + invoiceCallback.getFacturaId() +
                                    "\nBUYER: " + invoiceCallback.getBuyerTin() +
                                    "\nSELLER: " + invoiceCallback.getSellerTin() +
                                    "\nERROR: " + e.getMessage() + "\n";
                            log.error(stringBuilder, e);
                        }
                    }
                    if (StringUtils.isNotEmpty(sign)) {
                        updateSignatureData(sign, invoice);
                    } else {
                        String stringBuilder = "\n" + "INVOICE: " + invoiceCallback.getFacturaId() +
                                "\nSELLER: " + invoiceCallback.getSellerTin() +
                                "\nBUYER: " + invoiceCallback.getBuyerTin() +
                                "\nERROR: Sign content empty\n";
                        log.error(stringBuilder);
                    }
                    invoice.setSignatureContent(sign);
                    listInvoiceRepository.save(invoice);
                }
                invoice.setStatus(invoiceCallback.getState());
                listInvoiceRepository.save(invoice);

                checkForInvalidInvoice(invoice);
            }
        }
    }

    private boolean checkOrganization(final String sellerTin, final String buyerTin) {
        return organizationRepository.existsOrganizationByTinInAndDeletedIsFalse(Sets.newHashSet(sellerTin, buyerTin));
    }

    private void updateSignatureData(String sign, ListInvoice invoice) {
        Pkcs7Dto pkcs7Dto = eimzoClient.backendVerifyAttached(sign);
        if (pkcs7Dto.isSuccess()
                && pkcs7Dto.getPkcs7Info() != null
                && CollectionUtils.isNotEmpty(pkcs7Dto.getPkcs7Info().getSigners())) {
            SignatureData signatureData;

            if (StringUtils.isAnyEmpty(invoice.getSendBy(), invoice.getSendNumber())) {
                signatureData = parseSignatureData(pkcs7Dto.getPkcs7Info().getSigners().getFirst());
                invoice.setSenderData(signatureData);
            }
            if (StringUtils.isAnyEmpty(invoice.getApprovedBy(), invoice.getApprovedNumber()) && pkcs7Dto.getPkcs7Info().getSigners().size() > 1) {
                signatureData = parseSignatureData(pkcs7Dto.getPkcs7Info().getSigners().get(1));
                invoice.setApproverData(signatureData);
            }
        } else {
            log.debug("JSON: {}", pkcs7Dto);
        }
    }

    private void checkForInvalidInvoice(final ListInvoice listInvoice) {
        if (listInvoice.getInvoice() == null) {
            return;
        }
        final Invoice invoice = listInvoice.getInvoice();
        if (Status.ACCEPTED == listInvoice.getStatus()
                && invoice.getType().equals(FacturaType.EDITED.getId())
                && StringUtils.isNotBlank(invoice.getOldFacturaId())) {

            listInvoiceRepository.findOneByFacturaId(invoice.getOldFacturaId())
                    .ifPresent(invoice1 -> {
                        invoice1.setStatus(Status.INVALID);
                        listInvoiceRepository.save(invoice1);
                    });
        }
    }
}
