CREATE DATABASE football_booking_db;
USE football_booking_db;
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15),
    role ENUM('USER', 'ADMIN') DEFAULT 'USER'
);


CREATE TABLE fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    price_per_hour DECIMAL(10, 2) NOT NULL,
    status ENUM('AVAILABLE', 'UNAVAILABLE') DEFAULT 'AVAILABLE'
);

select * from fields;
CREATE TABLE items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL, -- Tên mặt hàng (ví dụ: Nước suối, Giày)
    price DECIMAL(10, 2) NOT NULL, -- Giá tiền (ví dụ: 10000 VND)
    stock INT NOT NULL DEFAULT 0 -- Số lượng tồn kho
);

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT, -- Liên kết với bảng bookings
    user_id BIGINT, -- Người thanh toán
    invoice_id BIGINT,
    amount DECIMAL(10, 2) NOT NULL, -- Tổng tiền
    payment_method ENUM('CASH', 'VNPAY') DEFAULT 'CASH', -- Phương thức thanh toán
    status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING', -- Trạng thái
    transaction_id VARCHAR(100), -- Mã giao dịch từ VNPay (nếu có)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (invoice_id) REFERENCES invoice(id)
);

CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    field_id BIGINT,
    user_id BIGINT,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELED') DEFAULT 'PENDING',
    FOREIGN KEY (field_id) REFERENCES fields(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
-- --để lưu các mặt hàng khách mua kèm mỗi lần đặt sân--
CREATE TABLE invoice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT,
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);






ALTER TABLE users ADD CONSTRAINT unique_username UNIQUE (username);
ALTER TABLE users ADD CONSTRAINT unique_email UNIQUE (email);
alter table bookings add column booking_date date;

ALTER TABLE fields ADD COLUMN image_url VARCHAR(255);

ALTER TABLE items ADD COLUMN image_url VARCHAR(255);




CREATE TABLE invoice_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (invoice_id) REFERENCES invoice(id),
    FOREIGN KEY (item_id) REFERENCES items(id)
);

ALTER TABLE invoice DROP COLUMN item_id;
ALTER TABLE invoice DROP COLUMN quantity;



