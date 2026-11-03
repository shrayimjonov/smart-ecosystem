package uz.uzkassa.smartpossupply.dto.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCompanyListDTO {
   CommonDTO company;
   CommonDTO creator;
   CommonDTO updater;
   LocalDateTime createdDate;
   LocalDateTime updatedDate;
}
