package uz.uzkassa.smartpos.dto.catalog;

public record CategoryResult(Long id, String name, Long parentId, String mxikCode, Long productCount, Long advanceCount,
                             Long creditCount) {
}
