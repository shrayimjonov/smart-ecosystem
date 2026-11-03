-- Stock
CREATE INDEX IF NOT EXISTS stock_deleted_idx  ON stock (deleted);

-- Stock Product
DROP INDEX IF EXISTS stock_adjustment_product_aggregation_product_id_idx;
DROP INDEX IF EXISTS stock_product_company_committent_id_idx;
DROP INDEX IF EXISTS stock_product_product_contract_id_idx;
DROP INDEX IF EXISTS stock_product_product_serial_id_idx;

-- Stock Product Aggregation
DROP INDEX IF EXISTS stock_product_aggregation_parent_code_idx;
DROP INDEX IF EXISTS stock_product_pallet_id_idx;

CREATE INDEX IF NOT EXISTS stock_product_aggregation_stock_product_id_idx  ON stock_product_aggregation (stock_product_id);
CREATE INDEX IF NOT EXISTS stock_product_aggregation_code_idx  ON stock_product_aggregation (code);
CREATE INDEX IF NOT EXISTS stock_product_aggregation_print_code_idx  ON stock_product_aggregation (print_code);
CREATE INDEX IF NOT EXISTS stock_product_aggregation_product_id_idx  ON stock_product_aggregation (product_id);
CREATE INDEX IF NOT EXISTS stock_product_transfer_type_idx  ON stock_product_aggregation (transfer_type);
CREATE INDEX IF NOT EXISTS stock_product_aggregation_deleted_idx  ON stock_product_aggregation (deleted);
CREATE INDEX IF NOT EXISTS stock_product_aggregation_code_transfer_type_deleted_idx  ON stock_product_aggregation (code, transfer_type, deleted);

-- Stock Product Aggregation Block

DROP INDEX IF EXISTS stock_product_aggregation_block_parent_code_idx;
DROP INDEX IF EXISTS stock_product_aggregation_block_pallet_id_idx;
DROP INDEX IF EXISTS stock_product_aggregation_block_box_id_idx;

CREATE INDEX IF NOT EXISTS stock_product_aggregation_block_stock_product_id_idx  ON stock_product_aggregation_block (stock_product_id);
CREATE INDEX IF NOT EXISTS stock_product_aggregation_block_code_idx  ON stock_product_aggregation_block (code);
CREATE INDEX IF NOT EXISTS stock_product_aggregation_block_print_code_idx  ON stock_product_aggregation_block (print_code);
CREATE INDEX IF NOT EXISTS stock_product_aggregation_block_transfer_type_idx  ON stock_product_aggregation_block (transfer_type);
CREATE INDEX IF NOT EXISTS stock_product_aggregation_block_product_id_idx  ON stock_product_aggregation_block (product_id);
CREATE INDEX IF NOT EXISTS stock_product_aggregation_block_deleted_idx  ON stock_product_aggregation_block (deleted);
CREATE INDEX IF NOT EXISTS stock_product_aggregation_block_code_transfer_type_deleted_idx  ON stock_product_aggregation_block (code, transfer_type, deleted);


-- Stock Product Mark

DROP INDEX IF EXISTS stock_product_mark_parent_code_idx;
DROP INDEX IF EXISTS stock_product_mark_pallet_id_idx;
DROP INDEX IF EXISTS stock_product_mark_box_id_idx;

CREATE INDEX IF NOT EXISTS stock_product_mark_stock_product_id_idx  ON stock_product_mark (stock_product_id);
CREATE INDEX IF NOT EXISTS stock_product_mark_code_idx  ON stock_product_mark (code);
CREATE INDEX IF NOT EXISTS stock_product_mark_print_code_idx  ON stock_product_mark (print_code);
CREATE INDEX IF NOT EXISTS stock_product_mark_product_id_idx  ON stock_product_mark (product_id);
CREATE INDEX IF NOT EXISTS stock_product_mark_transfer_type_idx  ON stock_product_mark (transfer_type);
CREATE INDEX IF NOT EXISTS stock_product_mark_deleted_idx  ON stock_product_mark (deleted);
CREATE INDEX IF NOT EXISTS stock_product_mark_code_transfer_type_deleted_idx  ON stock_product_mark (code, transfer_type, deleted);
