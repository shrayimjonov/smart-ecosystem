package uz.uzkassa.cts.dto.launcher;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LauncherRabbitPaginationRequestDto implements Serializable {
    private Integer page;
    private Integer size;
}
