package uz.uzkassa.cts.dto.candidate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.FullNameDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CandidateListAdminDTO extends FullNameDTO implements Serializable {

    String candidateNumber;

    String phone;

    String email;

    CommonDTO position;

    CommonDTO department;

    CommonDTO branch;

    CommonDTO company;

    CommonDTO vacancy;
}
