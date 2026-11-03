package uz.uzkassa.smartpos.repository.postgres.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.dto.advertisement.AdvertisementAdminDto;
import uz.uzkassa.smartpos.dto.advertisement.AdvertisementDeviceListDto;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.dto.base.PageDto;
import uz.uzkassa.smartpos.enums.DeviceType;
import uz.uzkassa.smartpos.enums.NotificationType;
import uz.uzkassa.smartpos.filter.AdvertisementFilter;
import uz.uzkassa.smartpos.repository.postgres.AdvertisementRepositoryCustom;

import java.sql.Array;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class AdvertisementRepositoryImpl implements AdvertisementRepositoryCustom {
    private final JdbcClient jdbcClient;

    @Override
    public PageDto<AdvertisementAdminDto> findAllByFilter(AdvertisementFilter filter) {
        StringBuilder selectSql = new StringBuilder("""
                select a.id,
                       a.created_by_id,
                       cr.last_name || ' ' || cr.first_name as createdByName,
                       a.created_date,
                       a.last_modified_by_id,
                       lt.last_name || ' ' || lt.first_name as lastModifiedByName,
                       a.last_modified_date,
                       a.type,
                       a.device_types,
                       a.serial_numbers,
                       c.id                                 as companyId,
                       c.inn,
                       c.name,
                       a.video_url\s""");
        StringBuilder countSql = new StringBuilder("select count(*) ");
        StringBuilder sql = new StringBuilder("""
                from advertisement a
                         left join users cr on cr.id = a.created_by_id
                         left join users lt on lt.id = a.last_modified_by_id
                         left join company c on a.company_id = c.id where 1=1\s""");
        var params = new HashMap<String, Object>();
        if (filter.getCompanyId() != null) {
            sql.append(" and a.company_id = :companyId ");
            params.put("companyId", filter.getCompanyId());
        }
        if (filter.getDeviceType() != null) {
            sql.append(" and :deviceType = any(a.device_types)");
            params.put("deviceType", filter.getDeviceType().name());
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            sql.append(" and :search = any (a.serial_numbers)");
            params.put("search", filter.getSearch());
        }
        selectSql.append(sql).append(" order by id desc limit :limit offset :offset");
        params.put("limit", filter.getSize());
        params.put("offset", filter.getOffset());
        countSql.append(sql);
        return new PageDto<>(
                jdbcClient.sql(selectSql.toString()).params(params).query((rs, rowNum) -> new AdvertisementAdminDto(rs.getLong("id"),
                        new CommonDTO(rs.getLong("created_by_id"), rs.getString("createdByName")),
                        rs.getObject("created_date", LocalDateTime.class),
                        new CommonDTO(rs.getLong("last_modified_by_id"), rs.getString("lastModifiedByName")),
                        rs.getObject("last_modified_date", LocalDateTime.class),
                        rs.getString("type") != null ? NotificationType.valueOf(rs.getString("type")) : null,
                        rs.getArray("device_types") != null ? Arrays.stream((String[]) rs.getArray("device_types").getArray()).map(DeviceType::valueOf).toList() : null,
                        rs.getArray("serial_numbers") != null ? Arrays.asList((String[]) rs.getArray("serial_numbers").getArray()) : null,
                        new CompanyBaseDTO(rs.getLong("companyId"), rs.getString("name"), rs.getString("inn")),
                        rs.getString("video_url")

                )).list(),
                filter.getPageable(),
                jdbcClient.sql(countSql.toString()).params(params).query(Long.class).optional().orElse(0L)
        );
    }

    @Override
    public List<AdvertisementDeviceListDto> advertisementForDevice(Device device) {
        return jdbcClient.sql("""
                        select a.id, a.type, a.device_types, a.video_url
                        from advertisement a
                        where (a.type = 'PUBLIC' and (coalesce(array_length(a.device_types, 1), 0) = 0 or :deviceType = any (device_types)))
                           or (a.type = 'PRIVATE' and a.company_id = :companyId and
                               (coalesce(array_length(a.device_types, 1), 0) = 0 or :deviceType = any (device_types)) and
                               (coalesce(array_length(a.serial_numbers, 1), 0) = 0 or :serialNumber = any (serial_numbers)))""")
                .param("deviceType", device.getType().name())
                .param("companyId", device.getCompanyId())
                .param("serialNumber", device.getSerialNumber())
                .query((rs, rowNum) -> {
                    Array deviceTypesArray = rs.getArray("device_types");
                    List<DeviceType> deviceTypes = null;
                    if (deviceTypesArray != null) {
                        deviceTypes = Arrays.stream((String[]) deviceTypesArray.getArray()).map(DeviceType::valueOf).collect(Collectors.toList());
                    }
                    String type = rs.getString("type");
                    return new AdvertisementDeviceListDto(rs.getLong("id"),
                            type != null ? NotificationType.valueOf(type) : null,
                            deviceTypes,
                            device.getSerialNumber(),
                            rs.getString("video_url"));
                }).list();
    }
}
