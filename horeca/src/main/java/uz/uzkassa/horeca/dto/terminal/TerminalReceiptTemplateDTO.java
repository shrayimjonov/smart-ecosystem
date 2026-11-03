package uz.uzkassa.horeca.dto.terminal;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.enums.terminal.TemplateAlignment;

@Getter
@Setter
public class TerminalReceiptTemplateDTO {
    private Long id;
    private Long companyId;
    private String companyName;
    private String headerText;
    private byte[] headerImage;
    private String headerImageContentType;
    private TemplateAlignment headerAlignment;
    private String footerText;
    private byte[] footerImage;
    private String footerImageContentType;
    private TemplateAlignment footerAlignment;
    private Long headerId;
    private String headerImageUrl;
    private Long footerId;
    private String footerImageUrl;
}
