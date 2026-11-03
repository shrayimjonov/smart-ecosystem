package uz.uzkassa.silen.domain.mongo;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import uz.uzkassa.silen.integration.turon.UtilisationRequest;
import uz.uzkassa.silen.integration.turon.UtilisationResponse;

import java.time.LocalDateTime;

@Data
@Document(value = "trace_utilisation")
public class TraceUtilisationRequest {
    @Id
    String id;
    String clientToken;
    String extension;
    String omsId;
    UtilisationResponse response;
    UtilisationRequest request;
    LocalDateTime time = LocalDateTime.now();
}
