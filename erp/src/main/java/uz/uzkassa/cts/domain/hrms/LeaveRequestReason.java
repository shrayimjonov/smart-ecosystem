package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.LocalizedEntity;
import uz.uzkassa.cts.enums.LeaveRequestReasonAbbreviation;
import uz.uzkassa.cts.enums.LeaveRequestReasonType;

import java.io.Serial;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "leave_request_reason")
@Entity
@SQLDelete(sql = "UPDATE leave_request_reason SET deleted='true' WHERE id=?")
public class LeaveRequestReason extends LocalizedEntity {

    @Serial
    private static final long serialVersionUID = 54853262643L;

    @Column(name = "abbreviation")
    @Enumerated(EnumType.STRING)
    LeaveRequestReasonAbbreviation abbreviation;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "description")
    String description;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    LeaveRequestReasonType type;

    @Column(name = "color")
    String color;
}
