package uz.uzkassa.smartpossupply.dto.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.06.2023
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SyncCustomerBranchPayload implements Serializable {

    String tin;

    Long customerId;

    int index;

    public SyncCustomerBranchPayload(int index) {
        this.index = index;
    }

    public SyncCustomerBranchPayload(String tin, Long customerId) {
        this.tin = tin;
        this.customerId = customerId;
    }
}
