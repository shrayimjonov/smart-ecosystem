package uz.uzkassa.cts.dto.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ReportTotalDTO implements Serializable {

    Long id;

    BigInteger total;

}
