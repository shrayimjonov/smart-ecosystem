package uz.uzkassa.silen.dto.filter;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.NotificationStatus;
import uz.uzkassa.silen.enumeration.NotificationTemplateType;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationFilter extends Filter {

    NotificationStatus status;

    NotificationTemplateType type;
}
