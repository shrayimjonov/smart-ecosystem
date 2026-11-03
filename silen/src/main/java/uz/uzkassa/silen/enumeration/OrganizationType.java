package uz.uzkassa.silen.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uz.uzkassa.silen.dto.SelectItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum OrganizationType implements Serializable {
    Alcohol(1, "Производители спирта"),
    Vine(1, "Ликероводочные заводы"),
    Beer(1, "Производители пива"),
    Tobacco(1, "Производители табака"),
    MedicinesManufacturer(1, "Производитель лекарственных средств"),
    WaterAndBeverageManufacturer(1, "Производитель воды и напитков"),
    Electronics(1, "Производитель техника"),

    Importer(2, "Импортер"),

    Wholesale(3, "Оптовые базы"),
    Delivery(3, "Спирто-перевозчики"),
    DistributorOfBeer(3, "Дистрибьютор Пиво"),
    TobaccoDistribution(3, "Дистрибьюторы табачной продукции"),
    AlcoholDistribution(3, "Дистрибьюторы алкогольной продукции"),
    TobaccoRetail(3, "Розничная реализация табачной продукции"),
    AlcoholRetail(3, "Розничная реализация алкогольной продукции"),
    DistributorOfMedicines(3, "Дистрибьютор лекарственных средств"),
    DistributorOfWaterAndBeverage(3, "Дистрибьютор вода и напитков"),
    DistributorOfElectronics(3, "Дистрибьютор техника"),
    CustomsWarehouse(2, "Таможенный склад"),

    Other(7, "Потребители спирта");

    private final String nameRu;
    private final String code;
    private final Integer id;
    private static final List<SelectItem> selectItemsForFilter = Arrays.asList(
            OrganizationType.Alcohol.toSelectItem(),
            OrganizationType.Vine.toSelectItem(),
            OrganizationType.Tobacco.toSelectItem()
    );

    OrganizationType(Integer id, String nameRu) {
        this.code = this.name();
        this.nameRu = nameRu;
        this.id = id;
    }

    public static List<SelectItem> getForFilter() {
        return selectItemsForFilter;
    }

    public static List<SelectItem> getAll() {
        List<SelectItem> items = new ArrayList<>(OrganizationType.values().length);
        for (OrganizationType item : OrganizationType.values()) {
            items.add(item.toSelectItem());
        }
        return items;
    }

    public SelectItem toSelectItem() {
        return new SelectItem(this.name(), this.getNameRu());
    }
}
