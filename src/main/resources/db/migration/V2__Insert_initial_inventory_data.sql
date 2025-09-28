-- V2__Insert_initial_inventory_data.sql
-- Insertar datos iniciales de inventario para productos de ejemplo
-- Solo se ejecutará si la tabla está vacía

-- Insertar datos de inventario de ejemplo solo si no existen registros
INSERT INTO inventory (id, product_id, quantity, created_at, updated_at)
SELECT * FROM (
    VALUES
        ('550e8400-e29b-41d4-a716-446655440001'::uuid, 'PROD-001', 100, NOW(), NOW()),
        ('550e8400-e29b-41d4-a716-446655440002'::uuid, 'PROD-002', 50, NOW(), NOW()),
        ('550e8400-e29b-41d4-a716-446655440003'::uuid, 'PROD-003', 75, NOW(), NOW()),
        ('550e8400-e29b-41d4-a716-446655440004'::uuid, 'PROD-004', 200, NOW(), NOW()),
        ('550e8400-e29b-41d4-a716-446655440005'::uuid, 'PROD-005', 25, NOW(), NOW())
) AS initial_data(id, product_id, quantity, created_at, updated_at)
WHERE NOT EXISTS (SELECT 1 FROM inventory LIMIT 1);