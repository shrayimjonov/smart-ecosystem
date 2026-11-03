package uz.uzkassa.smartpos.repository.postgres.terminal.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.filter.DeviceFilter;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepositoryCustom;

import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeviceRepositoryImpl implements DeviceRepositoryCustom {
    private final JdbcClient jdbcClient;

    @Override
    public List<CommonDTO> lookup(DeviceFilter filter) {
        StringBuilder sql = new StringBuilder("select id, serial_number as name from device where 1=1");
        var params = new HashMap<String, Object>();
        if (filter.getCompanyId() != null) {
            sql.append(" and company_id = :companyId");
            params.put("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            sql.append(" and branch_id = :branchId");
            params.put("branchId", filter.getBranchId());
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" and branch_id in (:branchIds)");
            params.put("branchIds", filter.getBranchIds());
        }
        if (CollectionUtils.isNotEmpty(filter.getTypes())) {
            sql.append(" and type in (:types)");
            params.put("types", filter.getTypes().stream().map(Enum::name).toList());
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            sql.append(" and serial_number ilike :search");
            params.put("search", filter.getSearchForQuery());
        }
        sql.append(" order by serial_number asc limit 10");
        return jdbcClient.sql(sql.toString()).params(params).query(CommonDTO.class).list();
    }
}
