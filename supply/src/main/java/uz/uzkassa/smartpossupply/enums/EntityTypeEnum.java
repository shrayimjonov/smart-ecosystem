package uz.uzkassa.smartpossupply.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Schema(enumAsRef = true)
public enum EntityTypeEnum implements Serializable {
    COMPANY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kompaniya";
                case cyrillic -> "Компания";
                default -> "Компания";
            };
        }
    },
    STOCK_IN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovar kirimi";
                case cyrillic -> "Товар кирими";
                default -> "Приход";
            };
        }
    },
    STOCK {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Zaxira";
                case cyrillic -> "Захира";
                default -> "Запас";
            };
        }
    },
    STOCK_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ombordagi tovarlar";
                case cyrillic -> "Омбордаги товарлар";
                default -> "Товары на складе";
            };
        }
    },
    STOCK_PRODUCT_AGGREGATION_HISTORY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kodlar tarixi";
                case cyrillic -> "Кодлар тарихи";
                default -> "История кодов";
            };
        }
    },
    STOCK_PRODUCT_MARK_HISTORY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Kodlar tarixi";
                case cyrillic -> "Кодлар тарихи";
                default -> "История кодов";
            };
        }
    },
    STOCK_PRODUCT_AGGREGATION_BLOCK_HISTORY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Guruhli agregatsiya kodlarining tarixi";
                case cyrillic -> "Гуруҳли агрегация кодларининг тарихи";
                default -> "История кодов групповой агреграции";
            };
        }
    },
    STOCK_BALANCE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Umumiy qoldiq";
                case cyrillic -> "Умумий қолдиқ";
                default -> "Общие остатки";
            };
        }
    },
    PRODUCT_REPORT_BY_CONTRACT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarlar qoldig'i shartnomalar bo'yicha";
                case cyrillic -> "Товарлар қолдиғи шартномалар бўйича";
                default -> "Остаток товаров по контрактам";
            };
        }
    },
    PRODUCT_REPORT_BY_SERIAL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarlar qoldig'i seriyalar bo'yicha";
                case cyrillic -> "Товарлар қолдиғи сериялар бўйича";
                default -> "Остаток товаров по сериям";
            };
        }
    },
    PRODUCT_REPORT_BY_COMMITTENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarlar qoldig'i komitent bo'yicha";
                case cyrillic -> "Товарлар қолдиғи комитент бўйича";
                default -> "Остаток товаров по комитен";
            };
        }
    },
    COMPANY_SETTINGS {
        @Override
        public String getName(LanguageEnum language) {
            return "COMPANY_SETTINGS";
        }
    },
    GENERIC_SETTINGS {
        @Override
        public String getName(LanguageEnum language) {
            return "GENERIC_SETTINGS";
        }
    },
    ORDER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Buyurtma";
                case cyrillic -> "Буюртма";
                default -> "Заказ";
            };
        }
    },
    ORDER_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Buyurtma";
                case cyrillic -> "Буюртма";
                default -> "Заказ";
            };
        }
    },
    CONTRACT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Shartnomalar";
                case cyrillic -> "Шартномалар";
                default -> "Договоры";
            };
        }
    },
    STOCK_IN_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovar kirimi";
                case cyrillic -> "Товар кирими";
                default -> "Приход товара";
            };
        }
    },
    STOCK_OUT_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarni jo'natish";
                case cyrillic -> "Товарни жўнатиш";
                default -> "Отгрузка товара";
            };
        }
    },
    STOCK_OUT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarni jo'natish";
                case cyrillic -> "Товарни жўнатиш";
                default -> "Отгрузка";
            };
        }
    },
    STOCK_TRANSFER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Topshirish";
                case cyrillic -> "Топшириш";
                default -> "Передача";
            };
        }
    },
    STOCK_ADJUSTMENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "To'g'rilash";
                case cyrillic -> "Тўғрилаш";
                default -> "Корректировка";
            };
        }
    },
    STOCK_ADJUSTMENT_AGGREGATION_CODES {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Коды";
                case cyrillic -> "Коды";
                default -> "Коды";
            };
        }
    },
    FACTURA {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Hisob-faktura";
                case cyrillic -> "Ҳисоб-фактура";
                default -> "Счет-фактура";
            };
        }
    },
    INVOICE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Hisob-faktura";
                case cyrillic -> "Ҳисоб-фактура";
                default -> "Счет-фактура";
            };
        }
    },
    AGENT_PERFORMANCE_REPORT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agentning ishlashi to'g'risidagi hisobot";
                case cyrillic -> "Агентнинг ишлаши тўғрисидаги ҳисобот";
                default -> "Отчет по работе агентов";
            };
        }
    },
    XTRACE_TOKEN {
        @Override
        public String getName(LanguageEnum language) {
            return "XTRACE_TOKEN";
        }
    },
    MAILING_LIST {
        @Override
        public String getName(LanguageEnum language) {
            return "MAILING_LIST";
        }
    },
    SCHEDULE_MESSAGE {
        @Override
        public String getName(LanguageEnum language) {
            return "SCHEDULE_MESSAGE";
        }
    },
    PLAN_ROUTE_ITEM {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yo'nalishlar";
                case cyrillic -> "Йўналишлар";
                default -> "Маршрут";
            };
        }
    },
    WAREHOUSE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ombor";
                case cyrillic -> "Омбор";
                default -> "Склад";
            };
        }
    },
    CUSTOMER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mijoz";
                case cyrillic -> "Мижоз";
                default -> "Клиент";
            };
        }
    },
    SUPPLIER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ta’minotchilar";
                case cyrillic -> "Таъминотчилар";
                default -> "Поставщик";
            };
        }
    },
    AGGREGATION_BLOCK {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agregatsiya bloki";
                case cyrillic -> "Агрегация блоки";
                default -> "Агрегации блок";
            };

        }
    },
    AGGREGATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agregatsiya";
                case cyrillic -> "Агрегация";
                default -> "Агрегация";
            };
        }
    },
    AGGREGATION_HISTORY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agregatsiya";
                case cyrillic -> "Агрегация";
                default -> "Агрегация";
            };
        }
    },
    TRANSACTION {
        @Override
        public String getName(LanguageEnum language) {
            return "TRANSACTION";
        }
    },
    DISCOUNT {
        @Override
        public String getName(LanguageEnum language) {
            return "DISCOUNT";
        }
    },
    MARK {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Markirovka";
                case cyrillic -> "Маркировка";
                default -> "Маркировка";
            };
        }
    },
    EXPORT_REPORT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Hisobot eksporti";
                case cyrillic -> "Ҳисобот экспорти";
                default -> "Экспорт отчета";
            };
        }
    },
    STOCK_RETURN {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarni qaytarish";
                case cyrillic -> "Товарни қайтариш";
                default -> "Возврат";
            };
        }
    },
    CASH_FLOW {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Naqd pul oqimi";
                case cyrillic -> "Нақд пул оқими";
                default -> "Поток наличных";
            };
        }
    },
    FINANCIAL_OPERATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Moliyaviy operatsiyalar";
                case cyrillic -> "Молиявий операциялар";
                default -> "Финансовая операция";
            };
        }
    },
    BRANCH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Filial";
                case cyrillic -> "Филиал";
                default -> "Филиал";
            };
        }
    },
    ACT_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarni qabul qilish-topshirish dalolatnomasi";
                case cyrillic -> "Товарни қабул қилиш-топшириш далолатномаси";
                default -> "Акт приема-передачи товара";
            };
        }
    },
    ACCEPTANCE_TRANSFER_ACT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Qabul qilish-topshirish dalolatnomalari";
                case cyrillic -> "Қабул қилиш-топшириш далолатномалари";
                default -> "Акты приема-передачи";
            };
        }
    },
    CATEGORY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Katalog";
                case cyrillic -> "Каталог";
                default -> "Каталог";
            };
        }
    },
    CUSTOMER_BRANCH {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Savdo nuqtalari";
                case cyrillic -> "Савдо нуқталари";
                default -> "Торговые точка";
            };
        }
    },
    USER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Foydalanuvchi";
                case cyrillic -> "Фойдаланувчи";
                default -> "Пользователь";
            };
        }
    },
    UNIT {
        @Override
        public String getName(LanguageEnum language) {
            return "UNIT";
        }
    },
    FACTURA_HOST {
        @Override
        public String getName(LanguageEnum language) {
            return "FACTURA_HOST";
        }
    },
    COUNTRY {
        @Override
        public String getName(LanguageEnum language) {
            return "COUNTRY";
        }
    },
    REGION {
        @Override
        public String getName(LanguageEnum language) {
            return "REGION";
        }
    },
    DISTRICT {
        @Override
        public String getName(LanguageEnum language) {
            return "DISTRICT";
        }
    },
    BANK {
        @Override
        public String getName(LanguageEnum language) {
            return "BANK";
        }
    },
    SHIFT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Smena";
                case cyrillic -> "Смена";
                default -> "Смена";
            };
        }
    },
    PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mahsulot";
                case cyrillic -> "Маҳсулот";
                default -> "Товар";
            };
        }
    },
    MARKETPLACE_APPLICATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mahsulot";
                case cyrillic -> "Маҳсулот";
                default -> "Товар";
            };
        }
    },
    MARK_ORDER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Markirovka kodlarini buyurtma qilish";
                case cyrillic -> "Маркировка кодларини буюртма қилиш";
                default -> "Заказ кодов маркировки";
            };
        }
    },
    PARTY_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Mahsulot partiyasi";
                case cyrillic -> "Маҳсулот партияси";
                default -> "Партия продукта";
            };
        }
    },
    PARTY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Partiya";
                case cyrillic -> "Партия";
                default -> "Партия";
            };
        }
    },
    AIC {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Aik";
                case cyrillic -> "Аик";
                default -> "Аик";
            };
        }
    },
    PRODUCT_SERIAL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Seriya";
                case cyrillic -> "Серия";
                default -> "Серия";
            };
        }
    },
    PRICE_LIST {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Narxlar ro'yxati";
                case cyrillic -> "Нархлар рўйхати";
                default -> "Прайс-лист";
            };
        }
    },
    FACTURA_DETAIL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Faktura maʼlumotlari";
                case cyrillic -> "Фактура маълумотлари";
                default -> "Детали фактуры";
            };
        }
    },
    STOCK_TRANSFER_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarni topshirish";
                case cyrillic -> "Товарни топшириш";
                default -> "Передача товара";
            };
        }
    },
    EXPEDITOR_STOCK_TRANSFER_DETAIL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ekspeditor topshirishi";
                case cyrillic -> "Экспедитор топшириши";
                default -> "Передача экспедитора";
            };
        }
    },
    STOCK_PRODUCT_AGGREGATION_BLOCK {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Guruhli agregatsiya";
                case cyrillic -> "Гуруҳли агрегация";
                default -> "Групповая агреграция";
            };
        }
    },
    STOCK_PRODUCT_AGGREGATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Агрегация";
                case cyrillic -> "Agregatsiya";
                default -> "Агрегация";
            };
        }
    },
    STOCK_PRODUCT_MARK {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Markirovka";
                case cyrillic -> "Маркировка";
                default -> "Маркировка";
            };
        }
    },
    STOCK_TRANSFER_PRODUCT_AGGREGATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agregatsiyani topshirish";
                case cyrillic -> "Агрегацияни топшириш";
                default -> "Передача агрегации";
            };
        }
    },
    STOCK_TRANSFER_PRODUCT_MARK {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Markirovkani topshirish";
                case cyrillic -> "Маркировкани топшириш";
                default -> "Передача маркировки";
            };
        }
    },
    AGGREGATION_ORDER {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agregatsiyaga buyurtma berish";
                case cyrillic -> "Агрегацияга буюртма бериш";
                default -> "Заказ агрегации";
            };
        }
    },
    ACT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Далолатнома";
                case cyrillic -> "Dalolatnoma";
                default -> "Акт";
            };
        }
    },
    PLAN_ROUTE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yo'nalish";
                case cyrillic -> "Йўналиш";
                default -> "Маршрут";
            };
        }
    },
    STOCK_ADJUSTMENT_PRODUCT_AGGREGATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agregatsiyani to'g'rilash";
                case cyrillic -> "Агрегацияни тўғрилаш";
                default -> "Корректировка агрегации";
            };
        }
    },
    STOCK_ADJUSTMENT_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarlarni to'g'rilash";
                case cyrillic -> "Товарларни тўғрилаш";
                default -> "Корректировка товаров";
            };
        }
    },
    STOCK_ADJUSTMENT_PRODUCT_MARK {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Markirovkani to'g'rilash";
                case cyrillic -> "Маркировкани тўғрилаш";
                default -> "Корректировка маркировки";
            };
        }
    },
    STOCK_ADJUSTMENT_PRODUCT_AGGREGATION_BLOCK {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Blok agregatsiyasini to'g'rilash";
                case cyrillic -> "Блок агрегациясини тўғрилаш";
                default -> "Корректировка агрегации блока";
            };
        }
    },
    WAY_BILL_PRODUCTS {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovarlar TTYU";
                case cyrillic -> "Товарлар ТТЮ";
                default -> "Товары ТТН";
            };
        }
    },
    LOYAL_CARD {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sodiqlik kartasi";
                case cyrillic -> "Содиқлик картаси";
                default -> "Карта лояльности";
            };
        }
    },
    WAY_BILL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tovar-transport yukxati";
                case cyrillic -> "Товар-транспорт юкхати";
                default -> "Товарно-транспортная накладная";
            };
        }
    },
    LOCALIZED_AUDITING_ENTITY {
        @Override
        public String getName(LanguageEnum language) {
            return "LOCALIZED_AUDITING_ENTITY";
        }
    },
    USER_SESSION {
        @Override
        public String getName(LanguageEnum language) {
            return "USER_SESSION";
        }
    },
    PLAN_ROUTE_TEMPLATE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yo'nalish rejasi shabloni";
                case cyrillic -> "Йўналиш режаси шаблони";
                default -> "Шаблон плана маршрута";
            };
        }
    },
    RECEIPT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Чек";
                case cyrillic -> "Chek";
                default -> "Чек";
            };
        }
    },
    COMPANY_AGGREGATION_INFO {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Agregatsiya maʼlumotlari";
                case cyrillic -> "Агрегация маълумотлари";
                default -> "Данные агрегации";
            };
        }
    },
    COMPANY_BANK {
        @Override
        public String getName(LanguageEnum language) {
            return "COMPANY_BANK";
        }
    },
    BRANCH_BANK {
        @Override
        public String getName(LanguageEnum language) {
            return "BRANCH_BANK";
        }
    },
    PARTNER {
        @Override
        public String getName(LanguageEnum language) {
            return "PARTNER";
        }
    },
    KPI_NEW_CLIENT {
        @Override
        public String getName(LanguageEnum language) {
            return "KPI_NEW_CLIENT";
        }
    },
    KPI_PLAN_ROUTE {
        @Override
        public String getName(LanguageEnum language) {
            return "KPI_PLAN_ROUTE";
        }
    },
    KPI_SALES_PLAN {
        @Override
        public String getName(LanguageEnum language) {
            return "KPI_SALES_PLAN";
        }
    },
    PRODUCT_CONTRACT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Shartnoma";
                case cyrillic -> "Шартнома";
                default -> "Контракт";
            };
        }
    },
    SALES_REPORT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sotuv xisoboti";
                case cyrillic -> "Сотув хисоботи";
                default -> "Отчеты: Сумма продаж + маршрут + клиент + ТТ";
            };
        }
    },
    MATERIAL_REPORT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Material xisoboti";
                case cyrillic -> "Материал хисоботи";
                default -> "Отчет материальный";
            };
        }
    },
    SALES_REPORT_BY_AGENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sotuv xisoboti";
                case cyrillic -> "Сотув хисоботи";
                default -> "Отчеты: Сумма продаж + клиент + ТТ";
            };
        }
    },
    ACCOUNT_RECEIVABLE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Дебиторская задолженность";
                case cyrillic -> "Дебиторская задолженность";
                default -> "Дебиторская задолженность";
            };
        }
    },
    SALES_CUSTOMER_REPORT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Отчет по продажам: Товары + клиенты";
                case cyrillic -> "Отчет по продажам: Товары + клиенты";
                default -> "Отчет по продажам: Товары + клиенты";
            };
        }
    },
    TERMINAL_LOG {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Terminal log";
                case cyrillic -> "Терминал лог";
                default -> "Терминал лог";
            };
        }
    },
    DEVICE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "NKM";
                case cyrillic -> "НКМ";
                default -> "ККМ";
            };
        }
    },
    DEVICE_MODEL {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "NKM modeli";
                case cyrillic -> "НКМ модели";
                default -> "Модель ККМ";
            };
        }
    },
    FISCAL_MODULE {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Fiscal modul";
                case cyrillic -> "Фискал модул";
                default -> "Фискальный модул";
            };
        }
    },
    COMPETITOR_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Raqobatchilarni mahsulotlari";
                case cyrillic -> "Рақобатчиларни маҳсулотлари";
                default -> "Товары конкурентов";
            };
        }
    },
    FISCALIZATION {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Fiskalizatsiya";
                case cyrillic -> "Фискализация";
                default -> "Фискализация";
            };
        }
    },
    CUSTOMER_BRANCH_PRODUCT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Tashrif kunidagi qoldiqlar";
                case cyrillic -> "Ташриф кунидаги қолдиклар";
                default -> "Остатки на день посещения";
            };
        }
    },
    ACTIVITY_TYPE {
        @Override
        public String getName(LanguageEnum language) {
            return "ACTIVITY_TYPE";
        }
    },
    EQUIPMENT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Uskunalar";
                case cyrillic -> "Ускуналар";
                default -> "Оборудование";
            };
        }
    },
    SALES_PRODUCT_REPORT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Sotuvlar bo‘yicha hisobot";
                case cyrillic -> "Сотувлар бўйича ҳисобот";
                default -> "Отчет по продажам";
            };
        }
    },
    ORDER_PRODUCT_EXPEDITOR_REPORT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Yig‘ma hisobot";
                case cyrillic -> "Йиғма ҳисобот";
                default -> "Сводный отчет";
            };
        }
    },
    ORDER_EXPEDITOR_REPORT {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "Ekspeditor bo'yicha buyurtmalar hisoboti";
                case cyrillic -> "Экспедитор бўйича буюртмалар ҳисоботи";
                default -> "Отчет по заказам экспедитора";
            };
        }
    },
    COMPANY_FIELD_HISTORY {
        @Override
        public String getName(LanguageEnum language) {
            return switch (language) {
                case uz -> "COMPANY_FIELD_HISTORY";
                case cyrillic -> "COMPANY_FIELD_HISTORY";
                default -> "COMPANY_FIELD_HISTORY";
            };
        }
    };

    private final String name;
    private final String code;

    EntityTypeEnum() {
        this.code = this.name();
        this.name = this.getName();
    }

    public abstract String getName(LanguageEnum language);

    public String getName() {
        return getName(SecurityUtils.getCurrentRequestLanguageEnum());
    }

    public static EntityTypeEnum get(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return Arrays.stream(values())
            .filter(status -> status.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown EntityTypeEnum enum value: " + code));
    }

    @JsonCreator
    public static EntityTypeEnum fromJson(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            String code = jsonNode.get("code").asText();
            return get(code);
        } else if (jsonNode.isTextual()) {
            return get(jsonNode.asText());
        }
        throw new IllegalArgumentException("Invalid JSON value for EntityTypeEnum");
    }
}
