package uz.uzkassa.smartpos.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum InstructorType {
    //common types
    COMMON_HOME_PAGE("COMMON", "Bosh sahifa", "Главная страница"),
    COMMON_COMPANY_DATA("COMMON", "Kompaniya-kompaniya tafsilotlari", "Компания-данные компании"),
    COMMON_FISCAL_REPORT_RECEIPT("COMMON", "Fiskal hisobotlar-cheklar", "Фискальные отчеты-чеки"),
    COMMON_ELECTRONIC_DOCUMENTS_INVOICE("COMMON", "Elektron hujjatlar - faktura", "Электронные документы - счет фактуры"),
    COMMON_SERVICES_ALL_SERVICES("COMMON", "Xizmatlar - Barcha xizmatlar", "Услуги - Все услуги"),
    COMMON_SETTINGS_PROFILE("COMMON", "Sozlamalar - profil", "Настройки- профиль"),

    //cabinet
    CATALOG("CABINET", "Katalog", "Каталог"),
    WAREHOUSE_GENERAL_BALANCE("CABINET", "Ombor-umumiy balanslar", "Склад-Общие остатки"),
    WAREHOUSE_BALANCE_BY_DAY("CABINET", "Ombor-kun bo'yicha qoldiqlar", "Склад-Остатки по дням"),
    WAREHOUSE_INCOME_OF_PRODUCT("CABINET", "Ombor-Prixod", "Склад-Приход"),
    WAREHOUSE_WRITE_OFF("CABINET", "Ombor-hisobdan chiqarish", "Склад-Списание"),
    WAREHOUSE_TRANSFER("CABINET", "Ombor - ko'chirish", "Склад- Перемещение"),
    REPORT_BY_PRODUCTS("CABINET", "Hisobotlar - mahsulot bo'yicha", "Отчеты - По товарам"),

    // fashion cabinet
    FASHION_CATALOG("FASHION", "Katalog", "каталог"),
    FASHION_WAREHOUSE_GENERAL_BALANCE("FASHION", "Ombor-umumiy balanslar", "Склад- Общие остатки"),
    FASHION_WAREHOUSE_INCOME_OF_PRODUCT("FASHION", "Ombor-Prixod", "склад - Приход"),
    FASHION_WAREHOUSE_WRITE_OFF("FASHION", "Ombor-hisobdan chiqarish", "склад-Списание"),
    FASHION_WAREHOUSE_TRANSFER("FASHION", "Ombor - ko'chirish", "Склад-Перемещение"),
    FASHION_REPORT_BY_PRODUCTS("FASHION", "Hisobotlar - mahsulot bo'yicha", "Отчеты - По товарам");

    private final String code;
    private final String parentCode;
    private final String nameUz;
    private final String nameRu;

    InstructorType(String parentCode, String nameUz, String nameRu) {
        this.code = name();
        this.nameUz = nameUz;
        this.parentCode = parentCode;
        this.nameRu = nameRu;
    }
}
