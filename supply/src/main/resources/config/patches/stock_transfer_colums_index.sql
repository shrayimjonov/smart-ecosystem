-- Stock Transfer
CREATE INDEX IF NOT EXISTS stock_transfer_company_id_idx  ON stock_transfer (company_id);
CREATE INDEX IF NOT EXISTS stock_transfer_from_branch_id_idx  ON stock_transfer (from_branch_id);
CREATE INDEX IF NOT EXISTS stock_transfer_to_branch_id_idx  ON stock_transfer (to_branch_id);
CREATE INDEX IF NOT EXISTS stock_transfer_from_warehouse_id_idx  ON stock_transfer (from_warehouse_id);
CREATE INDEX IF NOT EXISTS stock_transfer_to_warehouse_id_idx  ON stock_transfer (to_warehouse_id);
CREATE INDEX IF NOT EXISTS stock_transfer_number_idx  ON stock_transfer (transfer_number);
CREATE INDEX IF NOT EXISTS stock_transfer_deleted_idx  ON stock_transfer (deleted);

-- Stock Transfer Product
CREATE INDEX IF NOT EXISTS stock_transfer_product_deleted_idx  ON stock_transfer_product (deleted);
CREATE INDEX IF NOT EXISTS stock_transfer_product_stock_transfer_id_idx  ON stock_transfer_product (stock_transfer_id);

DROP INDEX IF EXISTS stock_transfer_product_from_product_id_idx;
DROP INDEX IF EXISTS stock_transfer_product_to_product_id_idx;

-- Stock Transfer Product Aggregation
DROP INDEX IF EXISTS stock_transfer_product_aggregation_product_id_idx;
DROP INDEX IF EXISTS stock_transfer_product_aggregation_stock_transfer_product_id_idx;

CREATE INDEX IF NOT EXISTS stock_transfer_product_aggregation_stp_id_idx  ON stock_transfer_product_aggregation (stock_transfer_product_id);
CREATE INDEX IF NOT EXISTS stock_transfer_product_aggregation_deleted_idx  ON stock_transfer_product_aggregation (deleted);

-- Stock Transfer Product Aggregation Block
DROP INDEX IF EXISTS stock_transfer_product_aggregation_block_id_idx;

CREATE INDEX IF NOT EXISTS stock_transfer_product_aggregation_block_stp_id_idx  ON stock_transfer_product_aggregation_block (stock_transfer_product_id);
CREATE INDEX IF NOT EXISTS stock_transfer_product_aggregation_block_deleted_idx  ON stock_transfer_product_aggregation_block (deleted);

-- Stock Transfer Product Mark
DROP INDEX IF EXISTS stock_transfer_product_mark_product_id_idx;

CREATE INDEX IF NOT EXISTS stock_transfer_product_mark_stock_transfer_product_id_idx  ON stock_transfer_product_mark (stock_transfer_product_id);
CREATE INDEX IF NOT EXISTS stock_transfer_product_mark_deleted_idx  ON stock_transfer_product_mark (deleted);

