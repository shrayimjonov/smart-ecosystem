package uz.uzkassa.cts.dto.feedback.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.FeedBackStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class FeedbackDetailCabinetDTO implements Serializable {

    Long id;

    int rate;

    String description;

    FeedBackStatus status;

    CommonCodeDTO type;

    CommonDTO customer;

    CommonDTO customerBranch;
}
