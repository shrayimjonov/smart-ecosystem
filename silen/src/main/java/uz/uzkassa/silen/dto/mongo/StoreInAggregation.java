package uz.uzkassa.silen.dto.mongo;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Document(collection = "store_in_aggregation_mark")
public class StoreInAggregation {
    @Id
    private String id;
    @Indexed
    private String parentCode;
    private LocalDateTime createdDate;
    @Indexed
    private String storeInId;
    private int quantity;
}
