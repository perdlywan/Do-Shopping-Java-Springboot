-- Indexes for 'products' table to speed up common queries
CREATE INDEX idx_products_deleted_at ON products (deleted_at);
CREATE INDEX idx_products_category_deleted_at ON products (category_id, deleted_at);

-- Indexes for 'orders' table
CREATE INDEX idx_orders_deleted_at ON orders (deleted_at);
CREATE INDEX idx_orders_customer_deleted_at ON orders (customer_id, deleted_at);
CREATE INDEX idx_orders_status_deleted_at ON orders (status, deleted_at);

-- Indexes for 'categories' table
CREATE INDEX idx_categories_deleted_at ON categories (deleted_at);

-- Indexes for 'customers' table
CREATE INDEX idx_customers_deleted_at ON customers (deleted_at);

-- Indexes for 'users' table
CREATE INDEX idx_users_deleted_at ON users (deleted_at);
