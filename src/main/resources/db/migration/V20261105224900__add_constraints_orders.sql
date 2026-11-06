ALTER TABLE orders
    ADD CONSTRAINT fk_orders_clients FOREIGN KEY (client_id) REFERENCES clients(id);