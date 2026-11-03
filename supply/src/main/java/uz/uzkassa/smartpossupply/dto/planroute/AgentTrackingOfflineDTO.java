package uz.uzkassa.smartpossupply.dto.planroute;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AgentTrackingOfflineDTO implements Serializable {

    @NotNull
    Long agentId;

    List<AgentTrackingOfflineLocationDTO> locations;
}
