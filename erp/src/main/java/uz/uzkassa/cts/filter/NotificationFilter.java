package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationFilter extends BaseFilter {

    Long receiverId;

    boolean unreadFirst = false;
}
