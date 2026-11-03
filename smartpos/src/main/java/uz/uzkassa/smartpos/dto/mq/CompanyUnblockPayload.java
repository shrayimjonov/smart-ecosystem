package uz.uzkassa.smartpos.dto.mq;

import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUnblockPayload implements Serializable {
    private Set<Long> companyIds;
}
