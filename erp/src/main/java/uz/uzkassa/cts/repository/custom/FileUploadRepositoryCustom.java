package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BaseFilter;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface FileUploadRepositoryCustom {

    List<FileUpload> list(Long entityId, String entityType);

    BigInteger listTotal(Long entityId, String entityType);

    ResultList<FileUpload> getListDocuments(BaseFilter filter);
}
