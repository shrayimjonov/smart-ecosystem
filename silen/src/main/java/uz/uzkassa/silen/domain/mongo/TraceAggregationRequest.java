package uz.uzkassa.silen.domain.mongo;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import uz.uzkassa.silen.integration.turon.CreateAggregationRequest;
import uz.uzkassa.silen.integration.turon.CreateAggregationResponse;

import java.time.LocalDateTime;

@Data
@Document(value = "trace_aggregation")
public class TraceAggregationRequest {
    @Id
    String id;
    String clientToken;
    String extension;
    String omsId;
    CreateAggregationResponse response;
    CreateAggregationRequest request;
    LocalDateTime time = LocalDateTime.now();
}
