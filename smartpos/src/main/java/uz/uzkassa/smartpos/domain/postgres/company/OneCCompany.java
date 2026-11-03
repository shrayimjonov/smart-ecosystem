package uz.uzkassa.smartpos.domain.postgres.company;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "one_c_company")
@Entity
public class OneCCompany {
    @Id
    private Long id;
    @NotBlank
    private String tin;
    @NotBlank
    private String name;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime firstTime = LocalDateTime.now();
    private LocalDateTime lastTime = LocalDateTime.now();
    private Long receiptCount = 0L;

    public void addReceipt() {
        if (receiptCount == 0) this.receiptCount = 0L;
        this.receiptCount += 1;
    }
}
