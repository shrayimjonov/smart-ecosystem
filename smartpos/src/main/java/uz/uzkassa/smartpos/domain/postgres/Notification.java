package uz.uzkassa.smartpos.domain.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.enums.BillingType;
import uz.uzkassa.smartpos.enums.NotificationCategory;
import uz.uzkassa.smartpos.enums.NotificationTarget;
import uz.uzkassa.smartpos.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "created_by_id")
    private Long createdById;
    @ManyToOne
    @JoinColumn(name = "created_by_id", insertable = false, updatable = false)
    private User createdBy;
    @LastModifiedDate
    private LocalDateTime lastModifiedAt = LocalDateTime.now();
    @Column(name = "last_modified_by_id")
    private Long lastModifiedById;
    @ManyToOne
    @JoinColumn(name = "last_modified_by_id", insertable = false, updatable = false)
    private User lastModifiedBy;
    @NotBlank
    private String titleUz;
    @NotBlank
    private String titleRu;
    @NotBlank
    private String titleCyrillic;
    @NotBlank
    @Column(columnDefinition = "text")
    public String messageUz;
    @NotBlank
    @Column(columnDefinition = "text")
    public String messageRu;
    @NotBlank
    @Column(columnDefinition = "text")
    public String messageCyrillic;
    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationCategory category = NotificationCategory.NEWS;
    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationTarget target = NotificationTarget.TERMINAL_AND_CABINET;
    @Enumerated(EnumType.STRING)
    private BillingType billingType;
    private String buttonUz;
    private String buttonRu;
    private String buttonCyrillic;
    private String buttonLink;
    @NotNull
    private LocalDateTime dateOfPublication = LocalDateTime.now();
    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;
    @OneToMany(mappedBy = "notification")
    private List<CompanyReadNotification> companies;
    @ManyToOne(fetch = FetchType.LAZY)
    private FileUpload image;
}
