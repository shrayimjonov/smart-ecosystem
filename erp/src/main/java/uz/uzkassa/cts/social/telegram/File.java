package uz.uzkassa.cts.social.telegram;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class File implements Serializable {
    private String file_name;
    private String mime_type;
    private String file_id;
    private Integer file_size;
    private Photo thumb;
}
