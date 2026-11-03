package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupervisorPlanRouteStatisticDTO {
    LocalDate visitedDate;
    Long totalCount;
    Long countOfVisited;
    Long countOfNotVisited;

    // When adding a new column to the query, make sure the field order is correct
    public SupervisorPlanRouteStatisticDTO(Date visitedDate,
                                           Long totalCount,
                                           Long countOfVisited,
                                           Long countOfNotVisited ) {
        this.visitedDate = visitedDate.toLocalDate();
        this.totalCount = totalCount;
        this.countOfVisited = countOfVisited;
        this.countOfNotVisited = countOfNotVisited;
    }
}
