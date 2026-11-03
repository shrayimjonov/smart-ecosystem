package uz.uzkassa.cts.dto.regionalassignment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import net.bytebuddy.asm.Advice;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegionalAssignmentListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7463641324L;

    Long id;

    CommonDTO creator;

    CommonDTO employee;

    CommonDTO region;

    CommonStatus status;

    LocalDateTime createdDate;

    LocalDateTime updatedDate;
}
