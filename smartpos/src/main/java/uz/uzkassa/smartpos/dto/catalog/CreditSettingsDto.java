package uz.uzkassa.smartpos.dto.catalog;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.domain.postgres.catalog.CreditSettings;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CreditSettingsDto implements Serializable {
    @NotNull
    private Long productId;
    @NotNull
    private Long branchId;
    private boolean creditEnabled;
    private CreditSettings creditSettings;
    private boolean advanceEnabled;
    private CreditSettings advanceSettings;
}
