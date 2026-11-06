ALTER TABLE payments
    ADD CONSTRAINT uk_payments_orders UNIQUE (order_id),
    ADD CONSTRAINT fk_payments_orders FOREIGN KEY (order_id) REFERENCES orders(id);