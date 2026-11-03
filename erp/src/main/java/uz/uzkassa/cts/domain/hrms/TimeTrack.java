package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.report.TimeTrackReportDTO;

import java.io.Serial;
import java.time.LocalDateTime;

@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "TimeTrackReportDTOMapper",
                        classes = @ConstructorResult(
                                targetClass = TimeTrackReportDTO.class,
                                columns = {
                                        @ColumnResult(name = "employeeId", type = Long.class),
                                        @ColumnResult(name = "employeeName", type = String.class),
                                        @ColumnResult(name = "departmentId", type = Long.class),
                                        @ColumnResult(name = "departmentName", type = String.class),
                                        @ColumnResult(name = "timeslotFromTime", type = String.class),
                                        @ColumnResult(name = "timeslotToTime", type = String.class),
                                        @ColumnResult(name = "timeslotTime", type = String.class),
                                        @ColumnResult(name = "lessTime", type = String.class),
                                        @ColumnResult(name = "overTime", type = String.class),
                                        @ColumnResult(name = "actualTime", type = String.class),
                                        @ColumnResult(name = "checkIn", type = String.class),
                                        @ColumnResult(name = "checkOut", type = String.class)
                                }
                        )
                )
        }
)
@Entity
@Table(name = "time_track")
@SQLDelete(sql = "UPDATE time_track SET deleted = 'true' WHERE id=?")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeTrack extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 35987658989874L;

    @Column(name = "employee_id", nullable = false)
    Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    User employee;

    @Column(name = "check_in")
    LocalDateTime checkIn;

    @Column(name = "check_out")
    LocalDateTime checkOut;

    @Column(name = "image_url")
    String imageUrl;

    @Column(name = "thumbnail_url")
    String thumbnailUrl;

    @Column(name = "video_url")
    String videoUrl;

    @Column(name = "event_external_id")
    String eventExternalId;

    @Column(name = "camera_ip")
    String cameraIp;
}
