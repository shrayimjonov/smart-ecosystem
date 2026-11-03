package uz.uzkassa.cts.repository.custom;

import uz.uzkassa.cts.domain.Document;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.DocumentFilter;

public interface DocumentRepositoryCustom {

    ResultList<Document> getResultList(DocumentFilter filter);
}
