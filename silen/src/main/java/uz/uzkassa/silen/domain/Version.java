package uz.uzkassa.silen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.VersionDTO;

@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "version")
@Getter
@Setter
@SQLDelete(sql = "UPDATE version SET deleted = 'true' WHERE id=?")
public class Version extends BaseIdentityEntity {

    @Column(name = "major")
    Integer major;

    @Column(name = "minor")
    Integer minor;

    @Column(name = "patch")
    Integer patch;

    @Column(name = "active", columnDefinition = "boolean default false")
    boolean active;

    public VersionDTO toDTO() {
        VersionDTO version = new VersionDTO();
        version.setId(getId());
        version.setMajor(getMajor());
        version.setMinor(getMinor());
        version.setPatch(getPatch());
        version.setActive(isActive());
        return version;
    }
}
