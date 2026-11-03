package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;

import java.io.Serial;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "terminal_log",
        indexes = {
                @Index(columnList = "serial_number", name = "terminal_log_serial_number_idx"),
                @Index(columnList = "file_id", name = "terminal_log_file_id_idx")
        })
@Entity
public class TerminalLog extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 45654646L;

    @Column(name = "serial_number")
    String serialNumber;

    @Column(name = "file_id")
    Long fileId;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    FileUpload file;
}
