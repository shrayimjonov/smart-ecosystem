package uz.uzkassa.smartpossupply.dto.mark;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.12.2022
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkStatisticsDTO {

    Long emittedTotal;

    Long appliedTotal;

    Long introducedTotal;

    Long writtenOFFTotal;

    Long withdrawnTotal;

    Long reservedNotUsedTotal;

    Long introducedReturnedTotal;

    Long disaggregatedTotal;

    Long waitShipmentTotal;

    Long exportedTotal;

    Long appliedNotPaidTotal;

    Long errorTotal;

    public MarkStatisticsDTO(
        Long emittedTotal,
        Long appliedTotal,
        Long introducedTotal,
        Long writtenOFFTotal,
        Long withdrawnTotal,
        Long reservedNotUsedTotal,
        Long introducedReturnedTotal,
        Long disaggregatedTotal,
        Long waitShipmentTotal,
        Long exportedTotal,
        Long appliedNotPaidTotal,
        Long errorTotal
    ) {
        this.emittedTotal = emittedTotal;
        this.appliedTotal = appliedTotal;
        this.introducedTotal = introducedTotal;
        this.writtenOFFTotal = writtenOFFTotal;
        this.withdrawnTotal = withdrawnTotal;
        this.reservedNotUsedTotal = reservedNotUsedTotal;
        this.introducedReturnedTotal = introducedReturnedTotal;
        this.disaggregatedTotal = disaggregatedTotal;
        this.waitShipmentTotal = waitShipmentTotal;
        this.exportedTotal = exportedTotal;
        this.appliedNotPaidTotal = appliedNotPaidTotal;
        this.errorTotal = errorTotal;
    }
}
