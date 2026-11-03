package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AgentStatisticsByProductDTO {

    Long agentId;

    List<StatisticByProductDTO> statistics;
}
