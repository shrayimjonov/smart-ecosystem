package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.FileUploadRepositoryCustom;

import java.math.BigInteger;
import java.util.List;

@SuppressWarnings("unused")
public class FileUploadRepositoryImpl extends BaseRepositoryImpl implements FileUploadRepositoryCustom {

    @Override
    public List<FileUpload> list(Long entityId, String entityType) {
        String sql = "select fu.* from file_upload fu \n" +
                " where fu.deleted = false \n" +
                " and fu.entity_id = " + entityId + "\n" +
                " and fu.entity_type ='" + EntityTypeEnum.get(entityType) + "'" +
                " order by fu.created_date desc";

        Query listQuery = entityManager.createNativeQuery(sql, FileUpload.class);

        return (List<FileUpload>) listQuery.getResultList();
    }

    @Override
    public BigInteger listTotal(Long entityId, String entityType) {
        String sql = "select count(fu.id) \n" +
                " from file_upload fu \n" +
                " where fu.deleted = false \n" +
                " and fu.entity_id = " + entityId + "\n" +
                " and fu.entity_type ='" + EntityTypeEnum.get(entityType) + "'";
        //sql.append(" and fu.content_type = '").append(MediaType.APPLICATION_PDF_VALUE).append("' ");

        Query countQuery = entityManager.createNativeQuery(sql);

        return BigInteger.valueOf((Long) countQuery.getSingleResult());
    }

    @Override
    public ResultList<FileUpload> getListDocuments(BaseFilter filter) {
        ResultList<FileUpload> resultList = new ResultList<>();
        String sql = "select fu.* \n" +
                getBaseDocumentSql(filter) +
                " order by fu." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName();

        Query listQuery = entityManager.createNativeQuery(sql, FileUpload.class);
        listQuery.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
        List<FileUpload> list = listQuery.getResultList();

        String countSql = "select count(fu.id) \n" +
                getBaseDocumentSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseDocumentSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from file_upload fu \n");
        sql.append(" where fu.deleted = false ");
        sql.append(" and fu.document_type ='").append(DocumentType.DOCUMENT.getCode()).append("'\n");
        sql.append(" and fu.entity_type ='").append(EntityTypeEnum.DOCUMENT.name()).append("'\n");

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(fu.file_name) ilike '%").append(filter.getSearch()).append("%' \n");
            sql.append(" or lower(fu.content_type) ilike '%").append(filter.getSearch()).append("%' \n");
            sql.append(") ");
        }
        return sql;
    }
}
