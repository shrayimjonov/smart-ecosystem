package uz.uzkassa.cts.dto.regionalassignment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegionalAssignmentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 863896287444L;

    Long employeeId;

    Long regionId;

    List<Long> districtIds;
}
