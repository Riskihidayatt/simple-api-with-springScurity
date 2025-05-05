-- Skrip SQL untuk Membuat Tabel Database Berdasarkan Entity Java (PostgreSQL)

-- 1. Tabel Roles (Direferensikan oleh user_roles)
CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL -- Menyimpan nama RoleType (ADMIN, STAFF, CUSTOMER)
);

-- 2. Tabel Users (Direferensikan oleh user_roles, customers, transactions)
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    is_enabled BOOLEAN DEFAULT TRUE,
    is_account_non_expired BOOLEAN DEFAULT TRUE,
    is_account_non_locked BOOLEAN DEFAULT TRUE,
    is_credentials_non_expired BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE
);

-- 3. Tabel Join User Roles (Mereferensikan users dan roles)
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 4. Tabel Customers (Mereferensikan users, Direferensikan oleh transactions)
CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birth_date DATE,
    birth_place VARCHAR(255),
    user_id UUID UNIQUE,             -- Foreign key ke user login (jika ada)
    created_by UUID,                 -- Foreign key ke user pembuat
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by UUID,                 -- Foreign key ke user pengupdate
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL, -- Atau CASCADE, tergantung aturan bisnis
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL, -- User dihapus, created_by jadi null
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL  -- User dihapus, updated_by jadi null
);

-- 5. Tabel Taxes (Direferensikan oleh product_taxes)
CREATE TABLE taxes (
    id UUID PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    percentage NUMERIC(19, 4) NOT NULL -- Meningkatkan skala untuk persentase (misal: 11.00 -> 11.0000)
);

-- 6. Tabel Product (Direferensikan oleh product_taxes, transaction_details)
CREATE TABLE product (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(19, 2) NOT NULL
);

-- 7. Tabel Join Product Taxes (Mereferensikan product dan taxes)
CREATE TABLE product_taxes (
    product_id UUID NOT NULL,
    tax_id UUID NOT NULL,
    PRIMARY KEY (product_id, tax_id),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    FOREIGN KEY (tax_id) REFERENCES taxes(id) ON DELETE CASCADE
);

-- 8. Tabel Transactions (Mereferensikan customers dan users, Direferensikan oleh transaction_details)
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    created_by UUID NOT NULL, -- Di entity Anda namanya 'createdBy', kolomnya 'created_by'
    net_amount NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    tax_amount NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    total_amount NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    transaction_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    payment_status VARCHAR(50) NOT NULL, -- Menyimpan status (PAID, NOT_PAID, CANCELLED)
    payment_method VARCHAR(50) NOT NULL, -- Menyimpan metode (CASH, TRANSFER, etc.)
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT, -- Jangan hapus customer jika ada transaksi
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT     -- Jangan hapus user jika pernah buat transaksi
);

-- 9. Tabel Transaction Details (Mereferensikan transactions dan product)
CREATE TABLE transaction_details (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    price_at_transaction NUMERIC(19, 2) NOT NULL, -- Harga produk saat transaksi
    total_amount NUMERIC(19, 2) NOT NULL, -- Total per baris (harga * qty + pajak baris tsb)
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE, -- Hapus detail jika transaksi dihapus
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE RESTRICT      -- Jangan hapus produk jika ada di detail transaksi
);

-- Pesan Selesai (Opsional)
-- SELECT 'Skema database berhasil dibuat berdasarkan entity.';