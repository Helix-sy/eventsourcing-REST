-- Create product_view table for product projections
CREATE TABLE IF NOT EXISTS product_view (
    product_id VARCHAR(255) PRIMARY KEY,
    brand VARCHAR(255) NOT NULL,
    category_id VARCHAR(255),
    category_code VARCHAR(255),
    price DECIMAL(19, 2) NOT NULL,
    views BIGINT NOT NULL DEFAULT 0,
    cart_additions BIGINT NOT NULL DEFAULT 0,
    purchases BIGINT NOT NULL DEFAULT 0,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    last_updated TIMESTAMP
);

-- Create index for faster brand and category queries
CREATE INDEX IF NOT EXISTS idx_product_view_brand ON product_view (brand);
CREATE INDEX IF NOT EXISTS idx_product_view_category_id ON product_view (category_id);

-- Create inventory_view table for inventory projections
CREATE TABLE IF NOT EXISTS inventory_view (
    product_id VARCHAR(255) PRIMARY KEY,
    stock INT NOT NULL DEFAULT 0,
    reorder_threshold INT NOT NULL DEFAULT 10,
    reorder_amount INT NOT NULL DEFAULT 50,
    reorder_in_progress BOOLEAN NOT NULL DEFAULT FALSE,
    last_updated TIMESTAMP
);

-- Create index for low stock queries
CREATE INDEX IF NOT EXISTS idx_inventory_view_stock ON inventory_view (stock);