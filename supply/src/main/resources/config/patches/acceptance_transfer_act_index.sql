CREATE INDEX IF NOT EXISTS acceptance_transfer_act_stock_out_id_idx  ON acceptance_transfer_act (stock_out_id);
CREATE INDEX IF NOT EXISTS acceptance_transfer_act_deleted_idx  ON acceptance_transfer_act (deleted);
