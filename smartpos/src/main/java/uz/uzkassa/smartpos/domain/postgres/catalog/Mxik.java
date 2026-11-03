package uz.uzkassa.smartpos.domain.postgres.catalog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikCheckInfoDTO;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikHistoryDTO;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Mxik {
    @Id
    @GeneratedValue(generator = "mxik_id_seq")
    @SequenceGenerator(name = "mxik_id_seq", sequenceName = "mxik_id_seq", allocationSize = 10)
    private Long id;

    @Column(columnDefinition = "varchar(17)", unique = true)
    private String mxik;
    @Column(columnDefinition = "varchar(1000)")
    private String nameUzLatin;
    @Column(columnDefinition = "varchar(1000)")
    private String nameCyril;
    @Column(columnDefinition = "varchar(1000)")
    private String nameRu;
    @Column(columnDefinition = "varchar(17)")
    private String barcode;
    private Integer label;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<MxikPackage> packages;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private MxikPackage defaultPackage;

    public String getName() {
        return switch (LanguageUtils.getLanguage()) {
            case uz -> nameUzLatin;
            case cyrillic -> nameCyril;
            case ru -> nameRu;
        };
    }

    public static Mxik merge(Mxik mxik, MxikHistoryDTO historyDTO, boolean updated) {
        mxik = Optional.ofNullable(mxik).orElse(new Mxik());
        if (updated) {
            mxik.mxik = historyDTO.getNewMxikCOde();
        } else {
            mxik.mxik = historyDTO.getMxik();
        }
        mxik.nameRu = historyDTO.getName();
        mxik.nameCyril = historyDTO.getName();
        mxik.nameUzLatin = historyDTO.getName();
        mxik.barcode = historyDTO.getInternationalCode();
        mxik.packages = historyDTO.getPackages();
        mxik.label = historyDTO.getLabel();
        if (CollectionUtils.isNotEmpty(historyDTO.getPackages())) {
            mxik.defaultPackage = historyDTO.getPackages().getFirst();
        }
        return mxik;
    }

    public static Mxik merge(Mxik mxik, MxikCheckInfoDTO dto) {
        mxik = Optional.ofNullable(mxik).orElse(new Mxik());
        mxik.mxik = dto.getMxikCode();
        mxik.nameRu = dto.getNameRu();
        mxik.nameCyril = dto.getNameUzCyrl();
        mxik.nameUzLatin = dto.getNameUzLatin();
        mxik.barcode = dto.getInternationalCode();
        mxik.packages = dto.getPackageNames();
        mxik.label = dto.getLabel();
        if (mxik.getDefaultPackage() == null && CollectionUtils.isNotEmpty(dto.getPackageNames())) {
            mxik.defaultPackage = dto.getPackageNames().getFirst();
        }
        return mxik;
    }
}
