package uz.uzkassa.smartpossupply.domain.factura;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "factura_validation")
public class FacturaValidation extends BaseEntity {

    @Column(name = "factura_id")
    Long facturaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", updatable = false, insertable = false)
    Factura factura;

    @Column(name = "is_valid")
    Boolean valid;

    @Column(name = "error", columnDefinition = "text")
    String error;

    @Column(name = "warning", columnDefinition = "text")
    String warning;

    @Column(name = "validation_issues", columnDefinition = "text")
    String validationIssues;

    @Column(name = "context_data", columnDefinition = "text")
    String contextData;

    @Column(name = "raw_response", columnDefinition = "text")
    String rawResponse;

    @CreatedDate
    @Column(name = "created_date")
    LocalDateTime createdDate = LocalDateTime.now();
}
