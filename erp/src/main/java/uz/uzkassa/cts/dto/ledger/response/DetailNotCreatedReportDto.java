package uz.uzkassa.cts.dto.ledger.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class DetailNotCreatedReportDto implements Serializable {
    private String reason;
    private Long count;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailNotCreatedReportDto that = (DetailNotCreatedReportDto) o;
        return reason.equals(that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reason);
    }
}
