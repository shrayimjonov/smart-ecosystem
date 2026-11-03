package uz.uzkassa.smartpossupply.dto.acceptancetransferact;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.01.2025
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptanceTransferActUpdateStatusDTO implements Serializable {

    String id;

    String status;

    String rejectNotes;
}
