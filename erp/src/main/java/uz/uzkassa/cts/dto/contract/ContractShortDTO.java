package uz.uzkassa.cts.dto.contract;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractShortDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1834728745L;

    Long id;

    String number;

    LocalDateTime createdDate;
}
