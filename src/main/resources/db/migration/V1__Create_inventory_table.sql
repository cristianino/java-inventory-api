-- Create inventory table
CREATE TABLE inventory (
    id UUID PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL UNIQUE,
    quantity INTEGER NOT NULL CHECK (quantity >= 0),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create indexes for better performance
CREATE INDEX idx_inventory_product_id ON inventory (product_id);
CREATE INDEX idx_inventory_quantity ON inventory (quantity);
CREATE INDEX idx_inventory_created_at ON inventory (created_at);
CREATE INDEX idx_inventory_updated_at ON inventory (updated_at);