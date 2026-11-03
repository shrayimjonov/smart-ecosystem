package uz.uzkassa.cts.dto.template;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ChannelType;
import uz.uzkassa.cts.enums.TemplateType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TemplateDTO implements Serializable {

    Long id;

    String subjectUzLatin;

    String subjectUzCyrillic;

    String subjectRu;

    String messageUzLatin;

    String messageUzCyrillic;

    String messageRu;

    TemplateType type;

    ChannelType channelType;
}
