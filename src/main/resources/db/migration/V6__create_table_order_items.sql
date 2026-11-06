CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    amount INT NOT NULL,
    total_price DECIMAL(8,2) NOT NULL
);