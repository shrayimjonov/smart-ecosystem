package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.repository.custom.StockItemRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;

import java.util.List;

@SuppressWarnings("unused")
public class StockItemRepositoryImpl extends BaseRepositoryImpl implements StockItemRepositoryCustom {

    @Override
    public List<CommonDTO> lookUp(StockFilter filter) {
        return switch (filter.getItemType()) {
            case PRODUCT -> getProductLookUp(filter);
            case DEVICE -> getDeviceLookUp(filter);
            case FISCAL_MODULE -> getFiscalModuleLookUp(filter);
            case ACCESSORY -> getAccessoryLookUp(filter);
        };
    }

    private List<CommonDTO> getProductLookUp(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct p.id, ");
        switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
            case ru -> sql.append("p.name_ru");
            case uz_latin -> sql.append("p.name_uz_latin");
            case uz_cyrillic -> sql.append("p.name_uz_cyrillic");
        }
        sql.append(" as name ");
        sql.append(" from product p ");
        sql.append(" inner join stock_item si on si.item_id = p.id");
        sql.append(" inner join stock s on s.id = si.stock_id");
        sql.append(" where p.deleted = false and s.deleted = false and si.deleted = false");
        sql.append(" and s.transfer_type='").append(filter.getTransferType()).append("'");
        sql.append(" and s.status='").append(StockStatus.COMPLETED.name()).append("'");
        sql.append(" and s.warehouse_id =").append(filter.getWarehouseId());
        sql.append(" and s.company_id =").append(filter.getCompanyId());

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(p.barcode)").append(filter.getLikeSearch());
            sql.append(" or lower(p.sku_number)").append(filter.getLikeSearch());
            sql.append(" or lower(p.upc_number)").append(filter.getLikeSearch());
            sql.append(" or lower(p.part_number)").append(filter.getLikeSearch());
            sql.append(" or lower(p.inventory_number)").append(filter.getLikeSearch());

            switch (SecurityUtils.getCurrentRequestLanguageEnum()) {
                case ru -> sql.append("or lower(p.name_ru)").append(filter.getLikeSearch());
                case uz_latin -> sql.append("or lower(p.name_uz_latin)").append(filter.getLikeSearch());
                case uz_cyrillic -> sql.append("or lower(p.name_uz_cyrillic)").append(filter.getLikeSearch());
            }
            sql.append(") ");
        }

        Query query = entityManager
                .createNativeQuery(sql.toString(), "StockItemLookUpMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
        }
        return (List<CommonDTO>) query.getResultList();
    }


    private List<CommonDTO> getDeviceLookUp(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct d.id, d.serial_number as name");
        sql.append(" from device d ");
        sql.append(" inner join stock_item si on si.item_id = d.id");
        sql.append(" inner join stock s on s.id = si.stock_id");
        sql.append(" where d.deleted = false and s.deleted = false and si.deleted = false");
        sql.append(" and s.transfer_type='").append(filter.getTransferType()).append("'");
        sql.append(" and s.status='").append(StockStatus.COMPLETED.name()).append("'");
        sql.append(" and s.warehouse_id =").append(filter.getWarehouseId());
        sql.append(" and s.company_id =").append(filter.getCompanyId());

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number)").append(filter.getLikeSearch());
            sql.append(") ");
        }

        Query query = entityManager
                .createNativeQuery(sql.toString(), "StockItemLookUpMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
        }
        return (List<CommonDTO>) query.getResultList();
    }

    private List<CommonDTO> getFiscalModuleLookUp(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct fm.id, fm.serial_number as name");
        sql.append(" from fiscal_module fm ");
        sql.append(" inner join stock_item si on si.item_id = fm.id");
        sql.append(" inner join stock s on s.id = si.stock_id");
        sql.append(" where fm.deleted = false and s.deleted = false and si.deleted = false");
        sql.append(" and s.transfer_type='").append(filter.getTransferType()).append("'");
        sql.append(" and s.status='").append(StockStatus.COMPLETED.name()).append("'");
        sql.append(" and s.warehouse_id =").append(filter.getWarehouseId());
        sql.append(" and s.company_id =").append(filter.getCompanyId());

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number)").append(filter.getLikeSearch());
            sql.append(") ");
        }

        Query query = entityManager
                .createNativeQuery(sql.toString(), "StockItemLookUpMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
        }
        return (List<CommonDTO>) query.getResultList();
    }

    private List<CommonDTO> getAccessoryLookUp(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct a.id, a.name as name");
        sql.append(" from accessory a ");
        sql.append(" inner join stock_item si on si.item_id = a.id");
        sql.append(" inner join stock s on s.id = si.stock_id");
        sql.append(" where a.deleted = false and s.deleted = false and si.deleted = false");
        sql.append(" and s.transfer_type='").append(filter.getTransferType()).append("'");
        sql.append(" and s.status='").append(StockStatus.COMPLETED.name()).append("'");
        sql.append(" and s.warehouse_id =").append(filter.getWarehouseId());
        sql.append(" and s.company_id =").append(filter.getCompanyId());

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(a.name)").append(filter.getLikeSearch());
            sql.append(") ");
        }

        Query query = entityManager
                .createNativeQuery(sql.toString(), "StockItemLookUpMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
        }
        return (List<CommonDTO>) query.getResultList();
    }
}
