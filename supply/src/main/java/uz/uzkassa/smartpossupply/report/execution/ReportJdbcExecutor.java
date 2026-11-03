package uz.uzkassa.smartpossupply.report.execution;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionTimedOutException;
import org.springframework.transaction.support.TransactionTemplate;
import uz.uzkassa.smartpossupply.report.engine.ReportErrorCode;
import uz.uzkassa.smartpossupply.report.engine.ReportException;
import uz.uzkassa.smartpossupply.report.engine.ReportQueryPlan;
import uz.uzkassa.smartpossupply.report.engine.ReportSqlQuery;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReportJdbcExecutor {

    private static final String QUERY_CANCELLED_SQL_STATE = "57014";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DataSourceTransactionManager transactionManager;
    private final Map<UUID, PreparedStatement> activeStatements = new ConcurrentHashMap<>();
    private final Set<UUID> runningExecutions = ConcurrentHashMap.newKeySet();
    private final Set<UUID> cancelledExecutions = ConcurrentHashMap.newKeySet();

    public ReportJdbcExecutor(@Qualifier("slaveDataSource") DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.transactionManager = new DataSourceTransactionManager(dataSource);
    }

    public ReportExecutionResult execute(UUID executionId, ReportQueryPlan plan) {
        int timeoutSeconds = Math.max(1, (int) Math.ceil(plan.statementTimeoutMs() / 1000.0));
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setReadOnly(true);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.setTimeout(timeoutSeconds);
        runningExecutions.add(executionId);
        try {
            ReportExecutionResult result = transactionTemplate.execute(status -> executeInTransaction(
                    executionId,
                    plan,
                    timeoutSeconds
            ));
            if (result == null) {
                throw new IllegalStateException("Report transaction returned no result");
            }
            return result;
        } catch (QueryTimeoutException | TransactionTimedOutException exception) {
            throw timeoutException();
        } catch (DataAccessException exception) {
            if (cancelledExecutions.contains(executionId)) {
                throw ReportException.limit(
                        ReportErrorCode.REQUEST_CANCELLED,
                        "Выполнение отчёта отменено"
                );
            }
            if (isQueryCancelled(exception)) {
                throw timeoutException();
            }
            if (exception instanceof CannotGetJdbcConnectionException) {
                throw new IllegalStateException("Не удалось подключиться к базе отчётов", exception);
            }
            throw new IllegalStateException("Не удалось выполнить запрос отчёта", exception);
        } finally {
            runningExecutions.remove(executionId);
            activeStatements.remove(executionId);
            cancelledExecutions.remove(executionId);
        }
    }

    public void cancel(UUID executionId) {
        if (!runningExecutions.contains(executionId)) {
            return;
        }
        cancelledExecutions.add(executionId);
        PreparedStatement statement = activeStatements.get(executionId);
        if (statement != null) {
            try {
                statement.cancel();
            } catch (SQLException ignored) {
                // The connection may already be closing because the request completed.
            }
        }
    }

    private ReportExecutionResult executeInTransaction(
            UUID executionId,
            ReportQueryPlan plan,
            int timeoutSeconds
    ) {
        jdbcTemplate.getJdbcTemplate().execute("SET LOCAL statement_timeout = " + plan.statementTimeoutMs());

        List<Map<String, Object>> rows = query(executionId, plan.dataQuery(), timeoutSeconds);
        plan.validateResultSize(rows.size());
        if (rows.isEmpty()) {
            return new ReportExecutionResult(rows, Map.of(), List.of(), List.of());
        }

        List<Map<String, Object>> grandTotalRows = query(
                executionId,
                plan.grandTotalQuery(),
                timeoutSeconds
        );
        List<Map<String, Object>> rowTotals = plan.rowTotalsQuery() == null
                ? List.of()
                : query(executionId, plan.rowTotalsQuery(), timeoutSeconds);
        List<Map<String, Object>> columnTotals = plan.columnTotalsQuery() == null
                ? List.of()
                : query(executionId, plan.columnTotalsQuery(), timeoutSeconds);

        plan.validateResultSize(rowTotals.size());
        plan.validateResultSize(columnTotals.size());
        Map<String, Object> grandTotal = grandTotalRows.isEmpty() ? Map.of() : grandTotalRows.getFirst();
        return new ReportExecutionResult(rows, grandTotal, rowTotals, columnTotals);
    }

    private List<Map<String, Object>> query(
            UUID executionId,
            ReportSqlQuery query,
            int timeoutSeconds
    ) {
        ensureNotCancelled(executionId);
        return jdbcTemplate.execute(
                query.sql(),
                new MapSqlParameterSource(query.parameters()),
                statement -> {
                    statement.setQueryTimeout(timeoutSeconds);
                    activeStatements.put(executionId, statement);
                    try {
                        ensureNotCancelled(executionId);
                    } catch (RuntimeException exception) {
                        activeStatements.remove(executionId, statement);
                        throw exception;
                    }
                    try (ResultSet resultSet = statement.executeQuery()) {
                        return rows(resultSet);
                    } finally {
                        activeStatements.remove(executionId, statement);
                    }
                }
        );
    }

    private List<Map<String, Object>> rows(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metadata = resultSet.getMetaData();
        List<Map<String, Object>> rows = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int column = 1; column <= metadata.getColumnCount(); column++) {
                row.put(metadata.getColumnLabel(column), resultSet.getObject(column));
            }
            rows.add(row);
        }
        return rows;
    }

    private void ensureNotCancelled(UUID executionId) {
        if (cancelledExecutions.contains(executionId) || Thread.currentThread().isInterrupted()) {
            throw ReportException.limit(
                    ReportErrorCode.REQUEST_CANCELLED,
                    "Выполнение отчёта отменено"
            );
        }
    }

    private boolean isQueryCancelled(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SQLException sqlException
                    && QUERY_CANCELLED_SQL_STATE.equals(sqlException.getSQLState())) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private ReportException timeoutException() {
        return ReportException.limit(
                ReportErrorCode.STATEMENT_TIMEOUT,
                "Время выполнения отчёта превысило допустимый лимит"
        );
    }
}
