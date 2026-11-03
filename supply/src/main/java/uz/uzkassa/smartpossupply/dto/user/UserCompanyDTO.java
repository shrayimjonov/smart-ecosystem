package uz.uzkassa.smartpossupply.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class UserCompanyDTO {

   @NotNull
   private Long userId;

   @NotEmpty
   private Set<Long> companyIds;
}
