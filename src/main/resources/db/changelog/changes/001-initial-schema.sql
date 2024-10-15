-- liquibase formatted sql

-- changeset Marcin:1728849499053-1
CREATE TABLE customers
(
    id          BIGINT AUTO_INCREMENT  NOT NULL,
    created_at  datetime DEFAULT NOW() NOT NULL,
    modified_at datetime               NULL,
    version     INT      DEFAULT 0     NULL,
    first_name  VARCHAR(255)           NOT NULL,
    last_name   VARCHAR(255)           NOT NULL,
    email       VARCHAR(255)           NOT NULL,
    address     VARCHAR(255)           NOT NULL,
    city        VARCHAR(255)           NOT NULL,
    postal_code VARCHAR(255)           NOT NULL,
    country     VARCHAR(255)           NOT NULL,
    phone       VARCHAR(255)           NOT NULL,
    CONSTRAINT pk_customers PRIMARY KEY (id)
);

-- changeset Marcin:1728849499053-2
CREATE TABLE order_items
(
    id          BIGINT AUTO_INCREMENT  NOT NULL,
    created_at  datetime DEFAULT NOW() NOT NULL,
    modified_at datetime               NULL,
    version     INT      DEFAULT 0     NULL,
    order_id    BIGINT                 NOT NULL,
    product_id  BIGINT                 NOT NULL,
    quantity    INT                    NOT NULL,
    unit_price  DECIMAL(10, 2)         NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id)
);

-- changeset Marcin:1728849499053-3
CREATE TABLE orders
(
    id             BIGINT AUTO_INCREMENT  NOT NULL,
    created_at     datetime DEFAULT NOW() NOT NULL,
    modified_at    datetime               NULL,
    version        INT      DEFAULT 0     NULL,
    customer_id    BIGINT                 NOT NULL,
    status         VARCHAR(255)           NOT NULL,
    payment_method VARCHAR(255)           NOT NULL,
    order_date     datetime               NOT NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

-- changeset Marcin:1728849499053-4
CREATE TABLE products
(
    id             BIGINT AUTO_INCREMENT  NOT NULL,
    created_at     datetime DEFAULT NOW() NOT NULL,
    modified_at    datetime               NULL,
    version        INT      DEFAULT 0     NULL,
    name           VARCHAR(255)           NOT NULL,
    `description`  VARCHAR(255)           NOT NULL,
    price          DECIMAL(10, 2)         NOT NULL,
    stock_quantity INT                    NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

-- changeset Marcin:1728849499053-5
ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customers (id);

-- changeset Marcin:1728849499053-6
ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

-- changeset Marcin:1728849499053-7
ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

