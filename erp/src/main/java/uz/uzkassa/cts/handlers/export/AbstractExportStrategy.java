package uz.uzkassa.cts.handlers.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.constants.FileConstants;

import static lombok.AccessLevel.PROTECTED;

@Component
@FieldDefaults(level = PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public abstract class AbstractExportStrategy implements ExportStrategy, FileConstants {

    ObjectMapper objectMapper;
}
