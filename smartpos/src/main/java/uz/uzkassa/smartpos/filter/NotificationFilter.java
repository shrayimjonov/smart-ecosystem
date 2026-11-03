package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;
import uz.uzkassa.smartpos.enums.BillingType;
import uz.uzkassa.smartpos.enums.NotificationCategory;
import uz.uzkassa.smartpos.enums.NotificationTarget;
import uz.uzkassa.smartpos.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class NotificationFilter extends PrimaryFilter {
    private NotificationType type;
    private BillingType billingType;
    private List<NotificationTarget> targets;
    private NotificationCategory category;
    private Long companyId;
    private LocalDateTime from;
    private LocalDateTime to;
    private Boolean read;
}
