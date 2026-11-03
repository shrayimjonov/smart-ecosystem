package uz.uzkassa.smartpossupply.dto.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
public class CommonDateDTO extends CommonDTO {


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime date;

    StockStatus status;

    public CommonDateDTO(Long id, String name, LocalDateTime date) {
        super(id, name);
        this.date = date;
    }

    public CommonDateDTO(Long id, String name, LocalDateTime date, StockStatus status) {
        super(id, name);
        this.date = date;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommonDateDTO)) {
            return false;
        }
        return Objects.equals(id, ((CommonDateDTO) o).id);
    }
}
