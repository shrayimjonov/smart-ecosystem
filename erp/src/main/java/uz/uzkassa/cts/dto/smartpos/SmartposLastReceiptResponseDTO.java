package uz.uzkassa.cts.dto.smartpos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created date time: 18.02.2025 16:29
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class SmartposLastReceiptResponseDTO implements Serializable {

    LocalDateTime receiptDateTime;

    String fiscalUrl;
}
