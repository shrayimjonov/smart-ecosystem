package uz.uzkassa.silen.dto.warehouse;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import uz.uzkassa.silen.domain.Income;
import uz.uzkassa.silen.dto.BaseAuditDTO;
import uz.uzkassa.silen.enumeration.IncomeStatus;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Income} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class IncomeDTO extends BaseAuditDTO implements Serializable {
    Long id;
    String number;
    IncomeStatus status = IncomeStatus.NEW;
    String statusName;
    Long storeId;
    String storeName;
    private String description;
    private List<IncomeItemDTO> items;

    public IncomeDTO(Income income) {
        this.id = income.getId();
        this.number = income.getNumber();
        this.status = income.getStatus();
        if (income.getStatus() != null) {
            this.statusName = income.getStatus().getText();
        }
        this.storeId = income.getStoreId();
        if (income.getStore() != null) {
            this.storeName = income.getStore().getName();
        }
        this.description = income.getDescription();
        if (CollectionUtils.isNotEmpty(income.getItems())) {
            this.items = income.getItems().stream()
                .map(IncomeItemDTO::new)
                .collect(Collectors.toList());
        }
    }
}
