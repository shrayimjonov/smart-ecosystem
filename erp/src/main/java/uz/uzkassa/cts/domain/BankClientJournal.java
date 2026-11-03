package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ServiceType;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bank_client_journal")
@Getter
@Setter
@SQLDelete(sql = "UPDATE bank_client_journal SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class BankClientJournal extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 865028335L;

    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "file_url")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType;

    @OneToMany(mappedBy = "bankClientJournal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BankClientDetail> details = new HashSet<>();

    public void addBankClientDetail(BankClientDetail detail) {
        detail.setBankClientJournal(this);
        this.details.add(detail);
    }
}
