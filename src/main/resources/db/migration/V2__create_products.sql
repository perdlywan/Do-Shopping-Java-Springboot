CREATE TABLE products (
  id CHAR(36) NOT NULL,
  category_id CHAR(36) NOT NULL,
  name VARCHAR(50) NOT NULL,
  price DECIMAL(18,2) NOT NULL,
  stock INT DEFAULT 0,
  description TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(50) NOT NULL,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by VARCHAR(50) NOT NULL,
  deleted_at DATETIME,
  deleted_by VARCHAR(50),
  PRIMARY KEY (id),
  FOREIGN KEY (category_id) REFERENCES categories(id)
);