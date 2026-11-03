package uz.uzkassa.horeca.dto.payload;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SyncFacturaByIdPayload implements Serializable {

    Long id;

    boolean isIncoming;
}
