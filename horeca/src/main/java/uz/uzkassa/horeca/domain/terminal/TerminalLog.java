package uz.uzkassa.horeca.domain.terminal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.base.SimpleEntity;

@Getter
@Setter
@Entity
@Table(name = "terminal_log")
public class TerminalLog extends SimpleEntity {
   
    @Column(name = "serial_number")
    private String serialNumber;

    @NotNull
    @Column(name = "db_file_id", nullable = false)
    Long dbFileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "db_file_id", updatable = false, insertable = false)
    FileUpload dbFile;
}
