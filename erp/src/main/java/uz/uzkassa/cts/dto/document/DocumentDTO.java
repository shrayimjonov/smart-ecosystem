package uz.uzkassa.cts.dto.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class DocumentDTO implements Serializable {

    Long id;

    String name;

    String path;

    Long folderId;
}
