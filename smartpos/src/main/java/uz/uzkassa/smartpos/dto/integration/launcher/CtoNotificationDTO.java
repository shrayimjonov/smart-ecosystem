package uz.uzkassa.smartpos.dto.integration.launcher;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class CtoNotificationDTO implements Serializable {
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

}
