package uz.uzkassa.smartpos.domain.postgres.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.dto.product.ColorGroupImageDTO;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "color_group_image")
public class ColorGroupImage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "color_group_id")
    private Long colorGroupId;
    @NotNull
    @Column(name = "branch_id")
    private Long branchId;
    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name = "color_group_id", referencedColumnName = "generic_id", insertable = false, updatable = false),
            @JoinColumn(name = "branch_id", referencedColumnName = "branch_id", insertable = false, updatable = false)
    }, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProductColorGroup colorGroup;

    @Column(name = "file_id")
    private Long fileId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", updatable = false, insertable = false)
    @ToString.Exclude
    private FileUpload file;

    @Column(name = "main", nullable = false, columnDefinition = "boolean default false")
    private boolean main;

    public ColorGroupImage(ColorGroupImage colorGroupImage, Long branchId) {
        this.id = colorGroupImage.getId();
        this.colorGroupId = colorGroupImage.getColorGroupId();
        this.branchId = branchId;
        this.fileId = colorGroupImage.getFileId();
        this.main = colorGroupImage.isMain();
    }

    public void setColorGroup(ProductColorGroup colorGroup) {
        this.colorGroupId = colorGroup.getGenericId();
        this.branchId = colorGroup.getBranchId();
        this.colorGroup = colorGroup;
    }

    public ColorGroupImageDTO toDTO() {
        ColorGroupImageDTO dto = new ColorGroupImageDTO();
        dto.setId(getId());
        dto.setFileId(getFileId());
        if (getFile() != null) {
            dto.setUrl(getFile().getUrl());
        }
        dto.setMain(isMain());
        return dto;
    }
}
