package uz.uzkassa.silen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.ChangelogDTO;

@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "changelog")
@Getter
@Setter
@SQLDelete(sql = "UPDATE changelog SET deleted = 'true' WHERE id=?")
public class Changelog extends BaseIdentityEntity {

    @Column(name = "version_id")
    Long versionId;

    @Column(name = "content")
    String content;

    @Column(name = "image_path")
    String imagePath;

    public ChangelogDTO toDTO() {
        ChangelogDTO changelogDTO = new ChangelogDTO();
        changelogDTO.setId(getId());
        changelogDTO.setContent(getContent());
        changelogDTO.setVersionId(getVersionId());
        changelogDTO.setImagePath(getImagePath());
        return changelogDTO;
    }
}
