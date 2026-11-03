package uz.uzkassa.smartpossupply.dto.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFacturaStatusPayload implements Serializable {

    Long id;

    String facturaId;

    String tin;

    boolean isIncoming;
}
