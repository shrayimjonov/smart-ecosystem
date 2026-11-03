package uz.uzkassa.smartpossupply.dto.launcher;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 11.12.2024
 */

@Getter
@Setter
@NoArgsConstructor
public class LauncherNotificationDTO implements Serializable {
    protected Long id;
    protected String createdBy;
    protected LocalDateTime createdDate;
    protected String lastModifiedBy;
    protected LocalDateTime lastModifiedDate;
    protected LocalDateTime statusDate;
    private String header;
    private String content;
    private Long subjectId;
    private String subjectName;
    private List<Long> terminalIds;
    private Long terminalCount;
    private BaseFilter filter;

    public LauncherNotificationDTO(Long id, String header, String subjectName, String content, String createdBy, BigInteger terminalCount, String createdDate) {
        this.id = id;
        this.header = header;
        this.subjectName = subjectName;
        this.content = content;
        this.createdBy = createdBy;
        this.terminalCount = terminalCount.longValue();
        this.createdDate = LocalDateTime.parse(createdDate.lastIndexOf(".") != -1 ?
                        createdDate.substring(0, createdDate.lastIndexOf(".")) : createdDate,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
