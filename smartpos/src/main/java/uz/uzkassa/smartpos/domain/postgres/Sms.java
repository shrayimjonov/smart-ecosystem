package uz.uzkassa.smartpos.domain.postgres;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.ProviderTypeEnum;
import uz.uzkassa.smartpos.enums.SmsStatusEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms")
@Getter
@Setter
public class Sms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    private boolean deleted = false;

    @Column(name = "recipient")
    private String recipient;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SmsStatusEnum status = SmsStatusEnum.PENDING;

    @Column(name = "provider_type")
    @Enumerated(EnumType.STRING)
    private ProviderTypeEnum providerType = ProviderTypeEnum.PLAY_MOBIL;

    @Column(name = "created_date")
    public LocalDateTime createdDate = LocalDateTime.now();
}
