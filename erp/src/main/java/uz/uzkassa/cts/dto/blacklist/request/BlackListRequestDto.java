package uz.uzkassa.cts.dto.blacklist.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BlackListRequestDto {
    @NotNull
    @NotEmpty
    private String tin;
    @NotNull
    @NotEmpty
    private String description;
}
