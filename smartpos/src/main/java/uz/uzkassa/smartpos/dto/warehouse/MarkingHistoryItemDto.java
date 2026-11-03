package uz.uzkassa.smartpos.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingHistoryItem;

import java.io.Serializable;
import java.util.HashSet;

/**
 * DTO for {@link MarkingHistoryItem}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkingHistoryItemDto implements Serializable {
    private Long id;
    @NotBlank
    private String clearCode;
    @NotBlank
    private String originalCode;
    private HashSet<String> children;
    @NotBlank
    private String status;
}
