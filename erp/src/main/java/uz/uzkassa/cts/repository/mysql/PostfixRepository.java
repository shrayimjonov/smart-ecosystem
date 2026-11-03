package uz.uzkassa.cts.repository.mysql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.postfix.PostfixMailboxDTO;
import uz.uzkassa.cts.dto.postfix.PostfixMailboxEmailDTO;
import uz.uzkassa.cts.mappings.mysql.PostfixDomainRowMapper;
import uz.uzkassa.cts.security.Md5CryptUtils;
import uz.uzkassa.cts.utils.DateUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class PostfixRepository {

    /*private final JdbcTemplate jdbcTemplate;

    public PostfixRepository(@Qualifier("mySqlJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createMailBox(PostfixMailboxDTO postfixMailboxDTO) {
        String date = DateUtil.LONG_FORMATTER.format(LocalDateTime.now());
        String domain = postfixMailboxDTO.getEmail().split("@")[1];

        StringBuilder aliasSql = new StringBuilder();
        aliasSql.append("insert into alias ");
        aliasSql.append(" (address, goto, domain, created, modified, active) ");
        aliasSql.append(" VALUES ");
        aliasSql.append("(");
        aliasSql.append("'").append(postfixMailboxDTO.getEmail()).append("',");
        aliasSql.append("'").append(postfixMailboxDTO.getEmail()).append("',");
        aliasSql.append("'").append(domain).append("',");
        aliasSql.append("'").append(date).append("',");
        aliasSql.append("'").append(date).append("',");
        aliasSql.append("1");
        aliasSql.append(")");

        try {
            //Create alias
            log.info("Create Alis MySQL query:" + aliasSql);
            jdbcTemplate.execute(aliasSql.toString());

            StringBuilder mailboxSql = new StringBuilder();
            mailboxSql.append("insert into mailbox(username, password, name, maildir, quota, local_part, domain, created, modified, active, phone, email_other, token, token_validity)");
            mailboxSql.append(" VALUES ");
            mailboxSql.append("(");
            mailboxSql.append("'").append(postfixMailboxDTO.getEmail()).append("',");
            mailboxSql.append("'").append(Md5CryptUtils.encodePassword(postfixMailboxDTO.getPassword())).append("',");
            mailboxSql.append("'").append(postfixMailboxDTO.getFirstName().concat(" ").concat(postfixMailboxDTO.getLastName())).append("',");
            mailboxSql.append("'").append(domain.concat("/").concat(postfixMailboxDTO.getEmail().split("@")[0]).concat("/")).append("',");
            mailboxSql.append(1024000000).append(",");
            mailboxSql.append("'").append(postfixMailboxDTO.getEmail().split("@")[0]).append("',");
            mailboxSql.append("'").append(domain).append("',");
            mailboxSql.append("'").append(date).append("',");
            mailboxSql.append("'").append(date).append("',");
            mailboxSql.append("1,");
            mailboxSql.append("'").append(Optional.ofNullable(postfixMailboxDTO.getPhone()).orElse("")).append("',");
            mailboxSql.append("'',");
            mailboxSql.append("'',");
            mailboxSql.append("'").append(date).append("'");
            mailboxSql.append(")");

            //Create mailbox
            log.info("Create EmailBox MySQL query:" + mailboxSql);
            jdbcTemplate.execute(mailboxSql.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void updateMailBoxStatus(String username, boolean active) {
        StringBuilder sql = new StringBuilder();
        sql.append("update mailbox set active =");
        if (active) {
            sql.append("1");
        } else {
            sql.append("0");
        }
        sql.append(" where username='").append(username).append("'");

        log.info("Update EmailBox Status MySQL query:" + sql);
        try {
            jdbcTemplate.execute(sql.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public List<CommonDTO> getDomains() {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from domain d where d.active=1");
        return jdbcTemplate.query(sql.toString(), new PostfixDomainRowMapper());
    }

    public void updateMailBoxEmail(PostfixMailboxEmailDTO postfixMailboxEmailDTO) {
        StringBuilder sql = new StringBuilder();
        sql.append("update mailbox set username ='").append(postfixMailboxEmailDTO.getEmail()).append("'");
        sql.append(" where username='").append(postfixMailboxEmailDTO.getOldEmail()).append("'");
        log.info("Update EmailBox Username MySQL query:" + sql);
        try {
            jdbcTemplate.execute(sql.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }*/
}
