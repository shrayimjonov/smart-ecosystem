package uz.uzkassa.smartpossupply.dto.report.metadata;

import uz.uzkassa.smartpossupply.enums.LanguageEnum;

import java.time.LocalDate;
import java.util.List;

/**
 * Localized metadata factory shared by the Report Builder API and its OpenAPI examples.
 */
public final class ReportMetadataFixtures {

    private static final List<ReportBuilderZone> DIMENSION_ZONES = List.of(
            ReportBuilderZone.ROWS,
            ReportBuilderZone.COLUMNS,
            ReportBuilderZone.FILTERS
    );
    private static final List<ReportBuilderZone> MEASURE_ZONES = List.of(ReportBuilderZone.MEASURES);

    private ReportMetadataFixtures() {
    }

    /**
     * Provides a deterministic Russian example for OpenAPI and contract tests.
     */
    public static ReportDatasetsResponse salesResponse() {
        return salesResponse(LanguageEnum.ru);
    }

    public static ReportDatasetsResponse salesResponse(LanguageEnum language) {
        return new ReportDatasetsResponse(List.of(sales(normalize(language))));
    }

    public static ReportDatasetMetadata sales(LanguageEnum language) {
        return sales(
                language,
                new ReportPeriodMetadata(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30))
        );
    }

    public static ReportDatasetMetadata sales(LanguageEnum language, ReportPeriodMetadata period) {
        return sales(language, period, new ReportLimitsMetadata(50_000, 100_000));
    }

    public static ReportDatasetMetadata sales(
            LanguageEnum language,
            ReportPeriodMetadata period,
            ReportLimitsMetadata limits
    ) {
        LanguageEnum normalizedLanguage = normalize(language);
        return new ReportDatasetMetadata(
                ReportDatasetType.SALES,
                text(normalizedLanguage, "Продажи (отгрузки)", "Sotuvlar (jo‘natmalar)", "Сотувлар (жўнатмалар)"),
                salesDimensions(normalizedLanguage),
                salesMeasures(normalizedLanguage),
                period,
                limits
        );
    }

    private static List<ReportDimensionMetadata> salesDimensions(LanguageEnum language) {
        return List.of(
                new ReportDimensionMetadata(
                        "document_date",
                        text(language, "Дата документа", "Hujjat sanasi", "Ҳужжат санаси"),
                        ReportDataType.DATE,
                        ReportValueFormat.DATE,
                        DIMENSION_ZONES,
                        true,
                        dateGranularities(language),
                        List.of()
                ),
                enumDimension(
                        "document_type",
                        text(language, "Тип документа", "Hujjat turi", "Ҳужжат тури"),
                        List.of(
                                enumValue(language, "OUT", "Отгрузка", "Jo‘natish", "Жўнатиш"),
                                enumValue(language, "RETURN", "Возврат", "Qaytarish", "Қайтариш")
                        )
                ),
                enumDimension(
                        "document_status",
                        text(language, "Статус документа", "Hujjat holati", "Ҳужжат ҳолати"),
                        List.of(
                                enumValue(language, "CHECKING", "На проверке", "Tekshiruvda", "Текширувда"),
                                enumValue(language, "CHECKING_ERROR", "Коды с ошибками", "Tekshiruvda xatolik", "Текширувда хатолик"),
                                enumValue(language, "DRAFT", "Черновик", "Qoralama", "Қоралама"),
                                enumValue(language, "CANCELLED", "Отменён", "Bekor qilingan", "Бекор қилинган"),
                                enumValue(language, "COMPLETED", "Завершён", "Bajarildi", "Бажарилди"),
                                enumValue(language, "DELETED", "Удалён", "O‘chirilgan", "Ўчирилган")
                        )
                ),
                enumDimension(
                        "payment_type",
                        text(language, "Тип оплаты", "To‘lov turi", "Тўлов тури"),
                        List.of(
                                enumValue(language, "CASH", "Наличными", "Naqd", "Нақд"),
                                enumValue(language, "APAY", "A-pay платёж", "A-pay", "A-pay"),
                                enumValue(language, "TRANSFER", "Перечисление", "Pul o‘tkazma", "Пул ўтказма"),
                                enumValue(language, "CARD", "Картой", "Karta", "Карта")
                        )
                ),
                stringDimension(language, "warehouse", "Склад", "Ombor", "Омбор"),
                stringDimension(language, "branch", "Филиал", "Filial", "Филиал"),
                stringDimension(language, "customer", "Клиент (юрлицо)", "Mijoz (yuridik shaxs)", "Мижоз (юридик шахс)"),
                stringDimension(language, "customer_outlet", "Торговая точка", "Savdo nuqtasi", "Савдо нуқтаси"),
                stringDimension(language, "customer_region", "Регион торговой точки", "Savdo nuqtasi hududi", "Савдо нуқтаси ҳудуди"),
                stringDimension(language, "agent", "Агент", "Agent", "Агент"),
                stringDimension(language, "product", "Товар", "Tovar", "Товар"),
                stringDimension(language, "product_category", "Категория", "Kategoriya", "Категория"),
                stringDimension(language, "brand", "Бренд", "Brend", "Бренд"),
                new ReportDimensionMetadata(
                        "marked",
                        text(language, "Маркируемый", "Markirovkalanadigan", "Маркировкаланадиган"),
                        ReportDataType.BOOLEAN,
                        ReportValueFormat.BOOLEAN,
                        DIMENSION_ZONES,
                        true,
                        List.of(),
                        List.of()
                )
        );
    }

    private static List<ReportMeasureMetadata> salesMeasures(LanguageEnum language) {
        return List.of(
                measure(language, "sales_amount", "Сумма продаж", "Sotuv summasi", "Сотув суммаси",
                        ReportDataType.DECIMAL, ReportValueFormat.MONEY),
                measure(language, "sales_amount_without_vat", "Сумма без НДС", "QQSsiz summa", "ҚҚСсиз сумма",
                        ReportDataType.DECIMAL, ReportValueFormat.MONEY),
                measure(language, "vat_amount", "НДС", "QQS", "ҚҚС",
                        ReportDataType.DECIMAL, ReportValueFormat.MONEY),
                measure(language, "quantity", "Количество", "Miqdor", "Миқдор",
                        ReportDataType.DECIMAL, ReportValueFormat.QUANTITY),
                measure(language, "discount_amount", "Скидка", "Chegirma", "Чегирма",
                        ReportDataType.DECIMAL, ReportValueFormat.MONEY),
                measure(language, "document_count", "Документы", "Hujjatlar", "Ҳужжатлар",
                        ReportDataType.INTEGER, ReportValueFormat.COUNT),
                measure(language, "sku_count", "SKU", "SKU", "SKU",
                        ReportDataType.INTEGER, ReportValueFormat.COUNT),
                measure(language, "active_outlet_count", "Активные торговые точки", "Faol savdo nuqtalari", "Фаол савдо нуқталари",
                        ReportDataType.INTEGER, ReportValueFormat.COUNT),
                measure(language, "average_check", "Средний чек", "O‘rtacha chek", "Ўртача чек",
                        ReportDataType.DECIMAL, ReportValueFormat.MONEY),
                unavailableMeasure(language, "cost_amount", "Себестоимость", "Tannarx", "Таннарх",
                        ReportDataType.DECIMAL, ReportValueFormat.MONEY),
                unavailableMeasure(language, "gross_margin", "Валовая маржа", "Yalpi marja", "Ялпи маржа",
                        ReportDataType.DECIMAL, ReportValueFormat.MONEY),
                unavailableMeasure(language, "gross_margin_percent", "Процент маржи", "Marja foizi", "Маржа фоизи",
                        ReportDataType.DECIMAL, ReportValueFormat.PERCENT)
        );
    }

    private static List<ReportDateGranularityMetadata> dateGranularities(LanguageEnum language) {
        return List.of(
                granularity(language, "document_day", ReportDateGranularity.DAY, "День", "Kun", "Кун", "yyyy-MM-dd"),
                granularity(language, "document_week", ReportDateGranularity.WEEK, "Неделя", "Hafta", "Ҳафта", "YYYY-'W'ww"),
                granularity(language, "document_month", ReportDateGranularity.MONTH, "Месяц", "Oy", "Ой", "yyyy-MM"),
                granularity(language, "document_quarter", ReportDateGranularity.QUARTER, "Квартал", "Chorak", "Чорак", "yyyy-'Q'q"),
                granularity(language, "document_year", ReportDateGranularity.YEAR, "Год", "Yil", "Йил", "yyyy")
        );
    }

    private static ReportDimensionMetadata stringDimension(
            LanguageEnum language,
            String key,
            String ru,
            String uz,
            String cyrillic
    ) {
        return new ReportDimensionMetadata(
                key,
                text(language, ru, uz, cyrillic),
                ReportDataType.STRING,
                ReportValueFormat.TEXT,
                DIMENSION_ZONES,
                true,
                List.of(),
                List.of()
        );
    }

    private static ReportDimensionMetadata enumDimension(
            String key,
            String name,
            List<ReportEnumValueMetadata> values
    ) {
        return new ReportDimensionMetadata(
                key,
                name,
                ReportDataType.ENUM,
                ReportValueFormat.ENUM,
                DIMENSION_ZONES,
                true,
                List.of(),
                values
        );
    }

    private static ReportMeasureMetadata measure(
            LanguageEnum language,
            String key,
            String ru,
            String uz,
            String cyrillic,
            ReportDataType dataType,
            ReportValueFormat format
    ) {
        return new ReportMeasureMetadata(
                key,
                text(language, ru, uz, cyrillic),
                dataType,
                format,
                MEASURE_ZONES,
                false,
                false,
                true
        );
    }

    private static ReportMeasureMetadata unavailableMeasure(
            LanguageEnum language,
            String key,
            String ru,
            String uz,
            String cyrillic,
            ReportDataType dataType,
            ReportValueFormat format
    ) {
        return new ReportMeasureMetadata(
                key,
                text(language, ru, uz, cyrillic),
                dataType,
                format,
                MEASURE_ZONES,
                false,
                true,
                false,
                false,
                text(
                        language,
                        "Себестоимость на отгрузке не заполняется",
                        "Jo‘natmada tannarx to‘ldirilmaydi",
                        "Жўнатмада таннарх тўлдирилмайди"
                )
        );
    }

    private static ReportDateGranularityMetadata granularity(
            LanguageEnum language,
            String key,
            ReportDateGranularity granularity,
            String ru,
            String uz,
            String cyrillic,
            String format
    ) {
        return new ReportDateGranularityMetadata(
                key,
                granularity,
                text(language, ru, uz, cyrillic),
                format
        );
    }

    private static ReportEnumValueMetadata enumValue(
            LanguageEnum language,
            String value,
            String ru,
            String uz,
            String cyrillic
    ) {
        return new ReportEnumValueMetadata(value, text(language, ru, uz, cyrillic));
    }

    private static String text(LanguageEnum language, String ru, String uz, String cyrillic) {
        return switch (normalize(language)) {
            case uz -> uz;
            case cyrillic -> cyrillic;
            case ru -> ru;
        };
    }

    private static LanguageEnum normalize(LanguageEnum language) {
        return language == null ? LanguageEnum.getDefaultLanguage() : language;
    }
}
