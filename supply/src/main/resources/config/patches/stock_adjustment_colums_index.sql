-- Stock Adjustment
CREATE INDEX IF NOT EXISTS stock_adjustment_company_id_idx  ON stock_adjustment (company_id);
CREATE INDEX IF NOT EXISTS stock_adjustment_branch_id_idx  ON stock_adjustment (branch_id);
CREATE INDEX IF NOT EXISTS stock_adjustment_warehouse_id_idx  ON stock_adjustment (warehouse_id);
CREATE INDEX IF NOT EXISTS stock_adjustment_number_idx  ON stock_adjustment (adjustment_number);
CREATE INDEX IF NOT EXISTS stock_adjustment_deleted_idx  ON stock_adjustment (deleted);


-- Stock Adjustment Product
CREATE INDEX IF NOT EXISTS stock_adjustment_product_deleted_idx  ON stock_adjustment_product (deleted);
CREATE INDEX IF NOT EXISTS stock_adjustment_product_stock_adjustment_id_idx  ON stock_adjustment_product (stock_adjustment_id);
CREATE INDEX IF NOT EXISTS stock_adjustment_product_product_id_idx  ON stock_adjustment_product (product_id);

DROP INDEX IF EXISTS stock_adjustment_product_product_id_idx;

-- Stock Adjustment Product Aggregation
DROP INDEX IF EXISTS stock_adjustment_product_aggregation_product_id_idx;
DROP INDEX IF EXISTS stock_adjustment_product_aggregation_stock_adjustment_product_id_idx;

CREATE INDEX IF NOT EXISTS stock_adjustment_product_aggregation_sap_id_idx  ON stock_adjustment_product_aggregation (stock_adjustment_product_id);
CREATE INDEX IF NOT EXISTS stock_adjustment_product_aggregation_deleted_idx  ON stock_adjustment_product_aggregation (deleted);

-- Stock Adjustment Product Aggregation Block
DROP INDEX IF EXISTS stock_adjustment_product_aggregation_block_product_id_idx;
DROP INDEX IF EXISTS stock_adjustment_product_aggregation_block_stock_adjustment_product_id_idx;

CREATE INDEX IF NOT EXISTS stock_adjustment_product_aggregation_block_sap_id_idx  ON stock_adjustment_product_aggregation_block (stock_adjustment_product_id);
CREATE INDEX IF NOT EXISTS stock_adjustment_product_aggregation_block_deleted_idx  ON stock_adjustment_product_aggregation_block (deleted);

-- Stock Adjustment Product Mark
DROP INDEX IF EXISTS stock_adjustment_product_mark_product_id_idx;
DROP INDEX IF EXISTS stock_adjustment_product_mark_stock_adjustment_product_id_idx;

CREATE INDEX IF NOT EXISTS stock_adjustment_product_mark_sap_id_idx  ON stock_adjustment_product_mark (stock_adjustment_product_id);
CREATE INDEX IF NOT EXISTS stock_adjustment_product_mark_deleted_idx  ON stock_adjustment_product_mark (deleted);
