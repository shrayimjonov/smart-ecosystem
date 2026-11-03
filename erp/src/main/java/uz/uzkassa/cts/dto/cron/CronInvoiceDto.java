package uz.uzkassa.cts.dto.cron;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class CronInvoiceDto implements Serializable {
    private Long tariffId;
    private String tariffName;
    private BigDecimal price = BigDecimal.ZERO;
    private Integer deviceCount = 0;
    private Long contractId;
    private BigDecimal tariffPrice = BigDecimal.ZERO;
}
