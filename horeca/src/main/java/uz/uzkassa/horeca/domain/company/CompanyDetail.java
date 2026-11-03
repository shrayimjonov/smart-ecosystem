package uz.uzkassa.horeca.domain.company;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.base.BaseEntity;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "company_detail",
        indexes = {
                @Index(columnList = "company_id", name = "company_detail_company_id_idx", unique = true)
        })
@SQLDelete(sql = "UPDATE company_detail set deleted='true' where id=?")
public class CompanyDetail extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 34545654L;

    @NotNull
    @Column(name = "company_id",nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "manager_id")
    Long managerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", updatable = false, insertable = false)
    User manager;

    @Column(name = "director_id")
    Long directorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id", updatable = false, insertable = false)
    User director;

    @Column(name = "accountant_id")
    Long accountantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountant_id", updatable = false, insertable = false)
    User accountant;

    @Column(name = "logo_id")
    Long logoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_id", insertable = false, updatable = false)
    FileUpload logo;

    @Column(name = "background_photo_id")
    Long backgroundPhotoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "background_photo_id", insertable = false, updatable = false)
    FileUpload backgroundPhoto;

    @Column(name = "allow_invoice_sync", columnDefinition = "boolean default false")
    boolean allowInvoiceSync = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompanyDetail)) {
            return false;
        }
        return id != null && id.equals(((CompanyDetail) o).id);
    }
}
