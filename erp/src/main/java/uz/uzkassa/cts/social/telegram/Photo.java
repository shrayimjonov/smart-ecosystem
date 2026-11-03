package uz.uzkassa.cts.social.telegram;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@ToString
@FieldDefaults(level = PRIVATE)
public class Photo implements Serializable {
    private String file_id;
    private Integer width;
    private Integer height;
    private Integer file_size;
}
