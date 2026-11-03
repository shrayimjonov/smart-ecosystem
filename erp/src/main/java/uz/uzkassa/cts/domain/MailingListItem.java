package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.dto.maillinglist.MailingListItemListDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.MailingListItemStatus;

import java.io.Serial;

@SqlResultSetMappings({
        @SqlResultSetMapping(name = "MailingListItemMapper",
                classes = {
                        @ConstructorResult(targetClass = MailingListItemListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "mailingListId", type = Long.class),
                                        @ColumnResult(name = "mailingListName", type = String.class),
                                        @ColumnResult(name = "status", type = String.class),
                                        @ColumnResult(name = "phone", type = String.class),
                                        @ColumnResult(name = "entityId", type = Long.class),
                                        @ColumnResult(name = "entityType", type = String.class),
                                        @ColumnResult(name = "customerName", type = String.class),
                                        @ColumnResult(name = "leadName", type = String.class)
                                })
                })
})

@Table(name = "mailing_list_item",
        indexes = {
                @Index(columnList = "entity_id", name = "mailing_list_item_entity_id_idx"),
                @Index(columnList = "entity_type", name = "mailing_list_item_entity_type_idx"),
                @Index(columnList = "phone", name = "mailing_list_item_phone_idx"),
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

    @Column(name = "entity_id")
    Long entityId;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @Column(name = "phone")
    String phone;

    @NotNull
    @Column(name = "mailing_list_id", nullable = false)
    Long mailingListId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mailing_list_id", updatable = false, insertable = false)
    MailingList mailingList;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    MailingListItemStatus status = MailingListItemStatus.PENDING;
}

