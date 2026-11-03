package uz.uzkassa.cts.dto.launcher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LauncherNotification {
    @JsonProperty("content")
    private String content;
    @JsonProperty("header")
    private String header;
    @JsonProperty("subject_id")
    private Long subjectId;

    public LauncherNotification(String content, String header, Long subjectId) {
        this.content = content;
        this.header = header;
        this.subjectId = subjectId;
    }

    public LauncherNotification() {
    }
}
