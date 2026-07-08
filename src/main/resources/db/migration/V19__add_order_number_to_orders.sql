ALTER TABLE orders ADD COLUMN order_number VARCHAR(50);

-- Backfill existing orders
UPDATE orders SET order_number = CONCAT('ORD-', UPPER(SUBSTRING(id, 1, 8)));

-- Add unique constraint
ALTER TABLE orders ADD CONSTRAINT uk_order_number UNIQUE (order_number);
