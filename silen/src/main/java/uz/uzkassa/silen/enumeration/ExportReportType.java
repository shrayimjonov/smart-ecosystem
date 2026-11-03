package uz.uzkassa.silen.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.security.SecurityUtils;

import java.io.Serializable;
import java.util.Map;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExportReportType implements Serializable {
    EXCEL_AGGREGATION_BY_PRODUCT {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Tovar bo'yicha aggregatsiya";
                case "cyrillic" -> "Товар бўйича аггрегация";
                default -> "Агрегация по продуктам";
            };
        }
    }, CSV_AGGREGATION_BY_PRODUCT {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Tovar bo'yicha aggregatsiya";
                case "cyrillic" -> "Товар бўйича аггрегация";
                default -> "Агрегация по продуктам";
            };
        }
    },

    MARK_KM {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Sotuv belgilari";
                case "cyrillic" -> "Сотув белгилари";
                default -> "Коды маркировок";
            };
        }
    },
    EXCEL_MARK_KM {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Sotuv belgilari";
                case "cyrillic" -> "Сотув белгилари";
                default -> "Коды маркировок";
            };
        }
    },
    CSV_MARK_KM {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Sotuv belgilari";
                case "cyrillic" -> "Сотув белгилари";
                default -> "Коды маркировок";
            };
        }
    },
    MARK_BLOCK {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Sotuv belgilari";
                case "cyrillic" -> "Сотув белгилари";
                default -> "Коды маркировок";
            };
        }
    },
    CSV_MARK_BLOCK {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Sotuv belgilari";
                case "cyrillic" -> "Сотув белгилари";
                default -> "Коды маркировок";
            };
        }
    },
    EXCEL_MARK_BLOCK {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Sotuv belgilari";
                case "cyrillic" -> "Сотув белгилари";
                default -> "Коды маркировок";
            };
        }
    },
    MARK_KA {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Sotuv belgilari";
                case "cyrillic" -> "Сотув белгилари";
                default -> "Коды маркировок";
            };
        }
    },
    SHIPMENT {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Yetkazib berish";
                case "cyrillic" -> "Етказиб бериш";
                default -> "Отгрузка";
            };
        }
    },
    EXCEL_SHIPMENT {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Yetkazib berish";
                case "cyrillic" -> "Етказиб бериш";
                default -> "Отгрузка";
            };
        }
    },
    CSV_SHIPMENT {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Yetkazib berish";
                case "cyrillic" -> "Етказиб бериш";
                default -> "Отгрузка";
            };
        }
    },
    CSV_UNUSED_MAKRS {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Отчет по маркировкам";
                case "cyrillic" -> "Отчет по маркировкам";
                default -> "Отчет по маркировкам";
            };
        }
    },
    DAILY_OPERATIONS {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Материальный отчет";
                case "cyrillic" -> "Материальный отчет";
                default -> "Материальный отчет";
            };
        }
    },
    MATERTIAL {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Материальный отчет";
                case "cyrillic" -> "Материальный отчет";
                default -> "Материальный отчет";
            };
        }
    },
    EXCEL_MATERTIAL {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Материальный отчет";
                case "cyrillic" -> "Материальный отчет";
                default -> "Материальный отчет";
            };
        }
    },
    CSV_PARTY_AGGREGATION {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Партии агрегации";
                case "cyrillic" -> "Партии агрегации";
                default -> "Партии агрегации";
            };
        }
    },
    EXCEL_PARTY_AGGREGATION {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Партии агрегации";
                case "cyrillic" -> "Партии агрегации";
                default -> "Партии агрегации";
            };
        }
    },
    PARTY_AGGREGATION_BLOCK {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Партии агрегации блок";
                case "cyrillic" -> "Партии агрегации  блок";
                default -> "Партии агрегации  блок";
            };
        }
    },
    CSV_PARTY_AGGREGATION_BLOCK {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Партии агрегации блок";
                case "cyrillic" -> "Партии агрегации  блок";
                default -> "Партии агрегации  блок";
            };
        }
    },
    EXCEL_PARTY_AGGREGATION_BLOCK {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Партии агрегации блок";
                case "cyrillic" -> "Партии агрегации  блок";
                default -> "Партии агрегации  блок";
            };
        }
    },
    UNIT_CODES_REPORT {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Партии агрегации блок";
                case "cyrillic" -> "Партии агрегации  блок";
                default -> "Партии агрегации  блок";
            };
        }
    },
    EXCEL_UNIT_CODES_REPORT {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Партии агрегации блок";
                case "cyrillic" -> "Партии агрегации  блок";
                default -> "Партии агрегации  блок";
            };
        }
    },
    BATCH_JSON_CONTENT {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Batch aggreagtion content";
                case "cyrillic" -> "Batch aggreagtion content";
                default -> "Batch aggreagtion content";
            };
        }
    },
    EXCEL_CUSTOMER_DISCOUNT {
        @Override
        public String getName(String language) {
            return switch (language) {
                case "uz" -> "Batch aggreagtion content";
                case "cyrillic" -> "Batch aggreagtion content";
                default -> "Batch aggreagtion content";
            };
        }
    };

    private final String name;
    private final String code;

    ExportReportType() {
        this.code = this.name();
        this.name = this.getName();
    }

    //
    public abstract String getName(String language);

    public String getName() {
        return getName(SecurityUtils.getCurrentLocale());
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static ExportReportType forValue(Map<String, String> value) {
        return ExportReportType.valueOf(value.get("code"));
    }
}
