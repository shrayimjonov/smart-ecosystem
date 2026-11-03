package uz.uzkassa.cts.dto.payload;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleMessagePayload implements Serializable {

    String subject;

    String message;

    List<Long> mailingListItemIds = new ArrayList<>();
}
