package uz.uzkassa.cts.dto.uploadfile.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DocumentType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class UploadFileResponse {

    Long id;

    String name;

    String fileName;

    String fileDownloadUri;

    String contentType;

    DocumentType documentType;

    Long size;

    String fileSize;

    String createdBy;

    LocalDateTime createdDate;
}
