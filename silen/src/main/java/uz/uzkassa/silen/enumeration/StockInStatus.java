package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import uz.uzkassa.silen.dto.EnumDTO;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StockInStatus implements Serializable {
    NEW {
        @Override
        public String getName(String language) {
            return "Новая";
        }
    },
    IN_PROGRESS {
        @Override
        public String getName(String language) {
            return "В процессе";
        }
    },
    COMPLETED {
        @Override
        public String getName(String language) {
            return "Завершено";
        }
    };

    private final String name;
    private final String code;

    StockInStatus() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(String language);

    public String getName() {
        return getName(SecurityUtils.getCurrentLocale());
    }

    public static StockInStatus forValue(@JsonProperty("code") String value) {
        Optional<StockInStatus> stockInStatus = EnumSet.allOf(StockInStatus.class)
            .stream().filter(status -> status.getCode().equals(value))
            .findFirst();
        return stockInStatus.orElse(StockInStatus.NEW);
    }

    public static List<EnumDTO> getAll() {
        ArrayList<EnumDTO> items = new ArrayList<>(StockInStatus.values().length);
        for (StockInStatus item : StockInStatus.values()) {
            items.add(item.toDto());
        }
        return items;
    }

    private EnumDTO toDto() {
        return new EnumDTO(this.name(), this.getName());
    }
}
