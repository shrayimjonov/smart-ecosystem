package uz.uzkassa.cts.dto.feedback;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class FeedbackDTO extends CommonDTO implements Serializable {

    Integer rate;
    String description;
    Long typeId;
    Long customerBranchId;
}
