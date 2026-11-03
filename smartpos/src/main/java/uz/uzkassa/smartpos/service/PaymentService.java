package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.receipt.Payment;
import uz.uzkassa.smartpos.dto.mq.PaymentPayload;
import uz.uzkassa.smartpos.enums.PaymentStatus;
import uz.uzkassa.smartpos.integration.meningUyim.MeningUyimClient;
import uz.uzkassa.smartpos.integration.meningUyim.MeningUyimPayment;
import uz.uzkassa.smartpos.integration.meningUyim.MeningUyimRequest;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.receipt.PaymentRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService extends SmartposService {
    private final PaymentRepository paymentRepository;
    private final MeningUyimClient meningUyimClient;
    private final RabbitMqProducer rabbitMqProducer;

    public void createPayment(final PaymentPayload payload) {
        Payment payment = Optional.ofNullable(payload.getPaymentId())
            .flatMap(paymentRepository::findById)
            .orElseGet(Payment::new);
        if (payment.getId() == null) {
            payment.setAccountCode(payload.getAccountCode());
            payment.setCompanyId(payload.getCompanyId());
            payment.setAmount(payload.getAmount());
            payment.setReceiptUid(payload.getReceiptUid());
        }
        try {
            final MeningUyimPayment response = meningUyimClient.prePayment(payment.getAccountCode(), payment.getAmount());
            payment.setExt_trs_id(response.getId() + "");
            payment.setDescription(response.getDescription());
            payment.setStatus(PaymentStatus.PENDING);
        } catch (final Exception ex) {
            payment.setDescription(ex.getMessage());
            payment.setStatus(PaymentStatus.ERROR);
        }
        payment = paymentRepository.save(payment);
        payload.setPaymentId(payment.getId());
        payload.setTrxId(payment.getExt_trs_id());

        rabbitMqProducer.send(payload, MqConstants.CONFIRM_PAYMENT);
    }

    public void confirmPayment(final PaymentPayload payload) {
        final Payment payment = paymentRepository.getReferenceById(payload.getPaymentId());
        try {
            final MeningUyimPayment response = meningUyimClient.confirmPayment(MeningUyimRequest.builder()
                .subscriberCode(payment.getAccountCode())
                .paymentId(Integer.valueOf(payment.getExt_trs_id()))
                .transactionId(payload.getPaymentId() + "")
                .amount(payment.getAmount())
                .build());
            payment.setDescription(response.getDescription());
            payment.setStatus(PaymentStatus.CONFIRMED);
        } catch (final Exception ex) {
            payment.setDescription(ex.getMessage());
            payment.setStatus(PaymentStatus.ERROR);
        }
        paymentRepository.save(payment);
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void sendErrorPayments() {
        final List<Payment> payments = paymentRepository.findAllByStatus(PaymentStatus.ERROR);
        for(final Payment payment : payments) {
            rabbitMqProducer.send(PaymentPayload.builder()
                    .paymentId(payment.getId())
                    .trxId(payment.getExt_trs_id())
                    .build(),
                StringUtils.isBlank(payment.getExt_trs_id()) ? MqConstants.CREATE_PAYMENT : MqConstants.CONFIRM_PAYMENT);
        }
    }
}
