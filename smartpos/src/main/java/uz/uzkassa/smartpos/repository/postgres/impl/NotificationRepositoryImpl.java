package uz.uzkassa.smartpos.repository.postgres.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import uz.uzkassa.smartpos.dto.CommonFileDTO;
import uz.uzkassa.smartpos.dto.UserShortDto;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.dto.notification.AdminNotificationDto;
import uz.uzkassa.smartpos.dto.notification.CabinetNotificationDto;
import uz.uzkassa.smartpos.enums.BillingType;
import uz.uzkassa.smartpos.enums.NotificationCategory;
import uz.uzkassa.smartpos.enums.NotificationTarget;
import uz.uzkassa.smartpos.enums.NotificationType;
import uz.uzkassa.smartpos.filter.NotificationFilter;
import uz.uzkassa.smartpos.repository.postgres.NotificationRepositoryCustom;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {
    private final JdbcClient jdbcClient;
    private final AdminNotificationRowMapper mapper = new AdminNotificationRowMapper();

    @Override
    public Page<AdminNotificationDto> findAllByFilterForAdmin(NotificationFilter filter) {
        StringBuilder selectSql = new StringBuilder("""
                select n.id                                                               as id,
                       created_at                                                         as createdAt,
                       cr.id                                                              as "createdBy.id",
                       cr.login                                                           as "createdBy.login",
                       (coalesce(cr.last_name, '') || ' ' || coalesce(cr.first_name, '')) as "createdBy.fullName",
                       created_at                                                         as lastModifiedAt,
                       lm.id                                                              as "lastModifiedBy.id",
                       lm.login                                                           as "lastModifiedBy.login",
                       (coalesce(lm.last_name, '') || ' ' || coalesce(lm.first_name, '')) as "lastModifiedBy.fullName",
                       title_uz                                                           as titleUz,
                       title_ru                                                           as titleRu,
                       title_cyrillic                                                     as titleCyrillic,
                       message_uz                                                         as messageUz,
                       message_ru                                                         as messageRu,
                       message_cyrillic                                                   as messageCyrillic,
                       type                                                               as type,
                       n.billing_type                                                     as billingType,
                       target                                                             as target,
                       button_uz                                                          as buttonUz,
                       button_ru                                                          as buttonRu,
                       button_cyrillic                                                    as buttonCyrillic,
                       button_link                                                        as buttonLink,
                       category                                                           as category,
                       date_of_publication                                                           as dateOfPublication,
                       c.id                                                               as "company.id",
                       c.name                                                             as "company.name",
                       c.inn                                                              as "company.tin",
                       f.id                                                               as "image.id",
                       f.url                                                              as "image.url",
                       (crn.id is not null)                                               as read\s""");
        StringBuilder countSql = new StringBuilder("select count(distinct n.id) ");
        Long companyJoinId = filter.getCompanyId() != null ? filter.getCompanyId() : SecurityUtils.getCurrentCompanyId();
        StringBuilder sql = new StringBuilder(" from notification n left join file_upload f on f.id = n.image_id \n" +
                "         left join company_read_notification crn on crn.notification_id = n.id and crn.company_id = " + companyJoinId +
                "         left join users cr on n.created_by_id = cr.id\n" +
                "         left join users lm on n.last_modified_by_id = lm.id\n" +
                "         left join company c on c.id = n.company_id\n");
        var params = new HashMap<String, Object>();
        sql.append(" where n.date_of_publication <= now()");
        if (filter.getType() != null) {
            sql.append(" and n.type = :type");
            params.put("type", filter.getType().name());
        }
        if (filter.getBillingType() != null) {
            sql.append(" and n.billing_type = :billingType");
            params.put("billingType", filter.getBillingType().name());
        }
        if (filter.getTargets() != null) {
            sql.append(" and n.target in (:targets)");
            params.put("targets", filter.getTargets().stream().map(Enum::name).toList());
        }
        if (filter.getCategory() != null) {
            sql.append(" and n.category = :category");
            params.put("category", filter.getCategory().name());
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and n.company_id = :companyId");
            params.put("companyId", filter.getCompanyId());
        }
        if (filter.getFrom() != null) {
            sql.append(" and n.created_at >= :from");
            params.put("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            sql.append(" and n.created_at <= :to");
            params.put("to", filter.getTo());
        }
        if (filter.getRead() != null) {
            if (filter.getRead()) {
                sql.append(" and crn.id is not null");
            } else {
                sql.append(" and crn.id is null");
            }
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            sql.append(" and (n.title_uz ilike :searchKey or " +
                    "n.title_ru ilike :searchKey or " +
                    "n.title_cyrillic ilike :searchKey or " +
                    "message_uz ilike :searchKey or " +
                    "message_ru ilike :searchKey or " +
                    "message_cyrillic ilike :searchKey)");
            params.put("searchKey", filter.getSearchForQuery());
        }
        selectSql.append(sql);
        countSql.append(sql);
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            selectSql.append(" order by ").append(filter.getOrderBy());
        } else {
            selectSql.append(" order by n.id desc");
        }
        selectSql.append(" limit ").append(filter.getSize()).append(" offset ").append(filter.getOffset());
        return new PageImpl<>(jdbcClient.sql(selectSql.toString()).params(params).query(mapper).list(),
                filter.getPageable(),
                jdbcClient.sql(countSql.toString()).params(params).query(Long.class).optional().orElse(0L));
    }

    @Override
    public List<CabinetNotificationDto> findAllByFilter(NotificationFilter filter) {
        StringBuilder selectSql = new StringBuilder("""
                select n.id as id,
                       n.created_at           as createdAt,
                       n.title_uz             as titleUz,
                       n.title_ru             as titleRu,
                       n.title_cyrillic       as titleCyrillic,
                       n.message_uz           as messageUz,
                       n.message_ru           as messageRu,
                       n.message_cyrillic     as messageCyrillic,
                       n.type                 as type,
                       n.target               as target,
                       n.button_uz            as buttonUz,
                       n.button_ru            as buttonRu,
                       n.button_cyrillic      as buttonCyrillic,
                       n.button_link          as buttonLink,
                       n.category             as category,
                       f.url                as imageUrl,
                       (crn.id is not null) as read \s
                       from notification n 
                       left join file_upload f on f.id = n.image_id 
                       left join company_read_notification crn on crn.notification_id = n.id and crn.company_id = :companyId""");
        var params = new HashMap<String, Object>();
        selectSql.append(" where n.date_of_publication <= now()");
        if (filter.getType() != null) {
            selectSql.append(" and n.type = :type");
            params.put("type", filter.getType().name());
        }
        if (filter.getBillingType() != null) {
            selectSql.append(" and (n.billing_type = :billingType or n.billing_type is null)");
            params.put("billingType", filter.getBillingType().name());
        }
        if (filter.getTargets() != null) {
            selectSql.append(" and n.target in (:targets)");
            params.put("targets", filter.getTargets().stream().map(Enum::name).toList());
        }
        if (filter.getCategory() != null) {
            selectSql.append(" and n.category = :category");
            params.put("category", filter.getCategory().name());
        }
        if (filter.getCompanyId() != null) {
            selectSql.append(" and (n.company_id = :companyId or n.type = 'PUBLIC')");
            params.put("companyId", filter.getCompanyId());
        }
        if (filter.getFrom() != null) {
            selectSql.append(" and n.created_at >= :from");
            params.put("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            selectSql.append(" and n.created_at <= :to");
            params.put("to", filter.getTo());
        }
        if (filter.getRead() != null) {
            if (filter.getRead()) {
                selectSql.append(" and crn.id is not null");
            } else {
                selectSql.append(" and crn.id is null");
            }
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            selectSql.append(" and (n.title_uz ilike :searchKey or " +
                    "n.title_ru ilike :searchKey or " +
                    "n.title_cyrillic ilike :searchKey or " +
                    "message_uz ilike :searchKey or " +
                    "message_ru ilike :searchKey or " +
                    "message_cyrillic ilike :searchKey)");
            params.put("searchKey", filter.getSearchForQuery());
        }
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            selectSql.append(" order by ").append(filter.getOrderBy());
        } else {
            selectSql.append(" order by n.id desc");
        }
        return jdbcClient.sql(selectSql.toString()).params(params).query(CabinetNotificationDto.class).list();
    }

    private static class AdminNotificationRowMapper implements RowMapper<AdminNotificationDto> {

        @Override
        public AdminNotificationDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            var dto = new AdminNotificationDto();
            dto.setId(rs.getLong("id"));
            dto.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
            if (rs.getObject("createdBy.id") != null) {
                UserShortDto createdBy = new UserShortDto();
                createdBy.setId(rs.getLong("createdBy.id"));
                createdBy.setLogin(rs.getString("createdBy.login"));
                createdBy.setFullName(rs.getString("createdBy.fullName"));
                dto.setCreatedBy(createdBy);
            }
            dto.setLastModifiedAt(rs.getTimestamp("lastModifiedAt").toLocalDateTime());
            if (rs.getObject("lastModifiedBy.id") != null) {
                UserShortDto lastModifiedBy = new UserShortDto();
                lastModifiedBy.setId(rs.getLong("lastModifiedBy.id"));
                lastModifiedBy.setLogin(rs.getString("lastModifiedBy.login"));
                lastModifiedBy.setFullName(rs.getString("lastModifiedBy.fullName"));
                dto.setLastModifiedBy(lastModifiedBy);
            }
            dto.setTitleUz(rs.getString("titleUz"));
            dto.setTitleRu(rs.getString("titleRu"));
            dto.setTitleCyrillic(rs.getString("titleCyrillic"));
            dto.setMessageUz(rs.getString("messageUz"));
            dto.setMessageRu(rs.getString("messageRu"));
            dto.setMessageCyrillic(rs.getString("messageCyrillic"));
            dto.setType(StringUtils.isNotBlank(rs.getString("type")) ? NotificationType.valueOf(rs.getString("type")) : null);
            dto.setBillingType(StringUtils.isNotBlank(rs.getString("billingType")) ? BillingType.valueOf(rs.getString("billingType")) : null);
            dto.setTarget(StringUtils.isNotBlank(rs.getString("target")) ? NotificationTarget.valueOf(rs.getString("target")) : null);
            dto.setButtonUz(rs.getString("buttonUz"));
            dto.setButtonRu(rs.getString("buttonRu"));
            dto.setButtonCyrillic(rs.getString("buttonCyrillic"));
            dto.setButtonLink(rs.getString("buttonLink"));
            dto.setCategory(StringUtils.isNotBlank(rs.getString("category")) ? NotificationCategory.valueOf(rs.getString("category")) : null);
            dto.setDateOfPublication(rs.getObject("dateOfPublication", LocalDateTime.class));
            dto.setRead(rs.getBoolean("read"));
            if (rs.getObject("company.id") != null) {
                CompanyBaseDTO company = new CompanyBaseDTO();
                company.setId(rs.getLong("company.id"));
                company.setTin(rs.getString("company.tin"));
                company.setName(rs.getString("company.name"));
                dto.setCompany(company);
            }
            if (rs.getObject("image.id") != null) {
                CommonFileDTO image = new CommonFileDTO();
                image.setId(rs.getLong("image.id"));
                image.setUrl(rs.getString("image.url"));
                dto.setImage(image);
            }
            return dto;
        }
    }
}
