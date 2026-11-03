package uz.uzkassa.smartpossupply.dto.markorder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.BufferStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkOrderListDTO implements Serializable {

    Long id;

    CommonDTO product;

    String orderNumber;

    BufferStatus bufferStatus;

    LocalDateTime orderDate;
}
