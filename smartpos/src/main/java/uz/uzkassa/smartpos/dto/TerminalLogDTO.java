package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.terminal.TerminalLog;

@Getter
@Setter
public class TerminalLogDTO {
    private Long id;
    private String serialNumber;
    private String dbFileUrl;

    public TerminalLogDTO(TerminalLog terminalLog) {
        this.id = terminalLog.getId();
        this.serialNumber = terminalLog.getSerialNumber();
        if (terminalLog.getDbFile() != null) {
            this.dbFileUrl = terminalLog.getDbFile().getUrl();
        }
    }
}
