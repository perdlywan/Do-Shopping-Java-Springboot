-- Update existing orders to match backend order_number format: ORD-yyyyMMdd-XXXX
-- using the 4 first characters of the UUID.
UPDATE orders 
SET order_number = CONCAT('ORD-', DATE_FORMAT(order_date, '%Y%m%d'), '-', UPPER(SUBSTRING(id, 1, 4)));
