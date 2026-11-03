package uz.uzkassa.smartpos.service.clickhouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.receipt.Receipt;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptDetail;
import uz.uzkassa.smartpos.dto.clickhouse.ClickhouseReceipt;
import uz.uzkassa.smartpos.dto.clickhouse.ClickhouseReceiptDetail;
import uz.uzkassa.smartpos.mappers.clickhouse.ClickhouseReceiptMapper;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClickhouseService {
    private final ObjectMapper objectMapper;
    private final ClickhouseReceiptMapper receiptMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${spring.kafka.receipt-topic}")
    private String RECEIPT_TOPIC;
    @Value("${spring.kafka.receipt-detail-topic}")
    private String RECEIPT_DETAIL_TOPIC;

    public void sendReceiptAndReceiptDetailToKafka(Receipt receipt) {
        ClickhouseReceipt clickhouseReceipt = receiptMapper.toClickhouseReceipt(
            receipt,
            receipt.getReceiptDetails().stream().map(ReceiptDetail::getId).collect(Collectors.toList()));
        try {
            kafkaTemplate.send(this.RECEIPT_TOPIC, objectMapper.writeValueAsString(clickhouseReceipt));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        receipt.getReceiptDetails().forEach(receiptDetail -> {
            ClickhouseReceiptDetail clickhouseReceiptDetail = receiptMapper.toClickhouseReceiptDetail(receiptDetail, clickhouseReceipt);
            clickhouseReceiptDetail.setMarks(receiptDetail.getMarks());
            try {
                kafkaTemplate.send(this.RECEIPT_DETAIL_TOPIC, objectMapper.writeValueAsString(clickhouseReceiptDetail));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }

}
