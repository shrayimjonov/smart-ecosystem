package uz.uzkassa.smartpossupply.domain.mailing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.MailingListItemStatus;

import java.io.Serial;

@Table(name = "mailing_list_item",
    indexes = {
        @Index(columnList = "mailing_list_id", name = "mailing_list_item_mailing_list_id_idx"),
        @Index(columnList = "status", name = "mailing_list_item_status_idx")
    }
)
@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailingListItem extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 5316900619L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, updatable = false)
    Company customer;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mailing_list_id", nullable = false, updatable = false)
    MailingList mailingList;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    MailingListItemStatus status = MailingListItemStatus.PENDING;
}

