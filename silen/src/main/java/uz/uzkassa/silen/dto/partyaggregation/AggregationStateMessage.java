package uz.uzkassa.silen.dto.partyaggregation;

import com.mongodb.lang.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.PersistenceCreator;
import uz.uzkassa.silen.enumeration.AggregationStatus;

import java.io.Serializable;

@ToString
@Getter
@Setter
public class AggregationStateMessage implements Serializable {

    String code;
    String status;
    String statusName;

    @PersistenceCreator
    public AggregationStateMessage(@Nullable String printCode, AggregationStatus status) {
        this.code = printCode;
        this.status = status.name();
        this.statusName = status.getText();
    }
}
