package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import uz.uzkassa.silen.enumeration.AggregationMode;
import uz.uzkassa.silen.enumeration.AggregationType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
public class AggregationBatchDTO implements Serializable {

    @Length(min = 20, max = 255)
    String code;

    @Size(min = 1,message = "Коды маркировки отсутствуют.")
    Set<String> marks = new LinkedHashSet<>();

    String productId;

    Integer quantity;

    String serialId;

    String organizationId;

    LocalDate expirationDate;

    LocalDate productionDate;

    @NotNull(message = "Тип пакета не выбран")
    Integer unit;

    AggregationMode aggregationMode;

    AggregationType type = AggregationType.BATCH;

    @Length(max = 255)
    String description;

    String login;
}
