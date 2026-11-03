package uz.uzkassa.smartpossupply.domain.telegram;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.TelegramBotType;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "telegram_integration",
        indexes = {
                @Index(columnList = "company_id", name = "telegram_integration_company_id_idx"),
                @Index(columnList = "bot_token", name = "telegram_integration_bot_token_idx"),
        })
public class TelegramIntegration extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 8593845333L;

    @Column(name = "bot_token", nullable = false)
    String botToken;

    @Column(name = "bot_username", nullable = false, unique = true)
    String botUsername;

    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    Company company;

    @Column(name = "bot_type")
    @Enumerated(EnumType.STRING)
    TelegramBotType botType;
}
