# Simple Api SpringBoot E-commerce API

Aplikasi Spring Boot ini menyediakan API untuk operasi e-commerce, termasuk manajemen pengguna, pelanggan, produk, transaksi, dan laporan.

## Prasyarat

- Java Development Kit (JDK) versi 17 atau lebih tinggi.
- Maven sebagai build tool.
- Database (misalnya PostgreSQL) yang sesuai dengan konfigurasi di `application.properties`.

## Menjalankan Aplikasi

1.  **Clone repository:**
    ```bash
    git clone <url_repository_anda>
    cd <nama_direktori_proyek>
    ```
2.  **Konfigurasi Database:**
    Pastikan konfigurasi database di `src/main/resources/application.properties` sudah sesuai dengan environment Anda. File `querysql/db_spring.sql` disediakan untuk membuat skema database awal.
3.  **Build Aplikasi:**
    ```bash
    ./mvnw clean package
    ```
4.  **Jalankan Aplikasi:**
    ```bash
    java -jar target/<nama_jar_file_anda>.jar
    ```
    Ganti `<nama_jar_file_anda>` dengan nama file JAR yang dihasilkan (biasanya ada di direktori `target/`).

Aplikasi akan berjalan pada port yang dikonfigurasi (default: 8080).

## File Pendukung

-   `querysql/db_spring.sql`: Skrip SQL untuk inisialisasi skema database.
-   `postman/SpringBoot Live Code API.postman_collection.json`: Koleksi Postman untuk menguji endpoint API.
-   Data dummy diinisialisasi melalui `DataInitializer.java` pada direktori `config` yang secara otomatis mengisi data awal ke database saat aplikasi pertama kali dijalankan.

## Daftar Endpoint API

Berikut adalah daftar endpoint yang tersedia:

### Autentikasi (`/api/auth`)

-   **`POST /api/auth/register-customer`**: Mendaftarkan customer baru.
    -   **Request Body**: `UserRegisterRequest` (`username`, `password`, `email`, `name`, `address`, `phoneNumber`)
    -   **Response**: `UserResponse`
    -   **Permissions**: Publik
-   **`POST /api/auth/register-staff`**: Mendaftarkan staff baru.
    -   **Request Body**: `UserRegisterRequest` (`username`, `password`, `email`, `name`, `address`, `phoneNumber`)
    -   **Response**: `UserResponse`
    -   **Permissions**: ADMIN
-   **`POST /api/auth/login`**: Login pengguna.
    -   **Request Body**: `LoginRequest` (`username`, `password`)
    -   **Response**: `JwtResponse` (berisi token JWT)
    -   **Permissions**: Publik

### Pelanggan (`/api/customers`)

-   **`POST /api/customers`**: Membuat pelanggan baru.
    -   **Request Body**: `CustomerRequest` (`name`, `address`, `phoneNumber`, `email`, `birthDate`)
    -   **Response**: `CustomerResponse`
    -   **Permissions**: ADMIN, STAFF
-   **`PUT /api/customers/{id}`**: Memperbarui pelanggan berdasarkan ID.
    -   **Request Body**: `CustomerRequest`
    -   **Response**: `CustomerResponse`
    -   **Permissions**: ADMIN, STAFF
-   **`GET /api/customers/{id}`**: Mendapatkan pelanggan berdasarkan ID.
    -   **Response**: `CustomerResponse`
    -   **Permissions**: ADMIN, STAFF
-   **`GET /api/customers`**: Mendapatkan semua pelanggan (dengan paginasi).
    -   **Query Params**: `page`, `size`, `sort`
    -   **Response**: Daftar `CustomerResponse`
    -   **Permissions**: ADMIN, STAFF
-   **`DELETE /api/customers/{id}`**: Menghapus pelanggan berdasarkan ID.
    -   **Permissions**: ADMIN

### Produk (`/api/products`)

-   **`POST /api/products`**: Membuat produk baru.
    -   **Request Body**: `ProductRequest` (`name`, `description`, `price`, `stock`)
    -   **Response**: `ProductResponse`
    -   **Permissions**: ADMIN, STAFF
-   **`PUT /api/products/{id}`**: Memperbarui produk berdasarkan ID.
    -   **Request Body**: `ProductRequest`
    -   **Response**: `ProductResponse`
    -   **Permissions**: ADMIN, STAFF
-   **`GET /api/products/{id}`**: Mendapatkan produk berdasarkan ID.
    -   **Response**: `ProductResponse`
    -   **Permissions**: ADMIN, STAFF (atau `isAuthenticated()` jika semua boleh lihat)
-   **`GET /api/products`**: Mendapatkan semua produk (dengan paginasi).
    -   **Query Params**: `page`, `size`, `sort`
    -   **Response**: Daftar `ProductResponse`
    -   **Permissions**: ADMIN, STAFF (atau `isAuthenticated()` jika semua boleh lihat)
-   **`DELETE /api/products/{id}`**: Menghapus produk berdasarkan ID.
    -   **Permissions**: ADMIN

### Transaksi (`/api/transactions`)

-   **`POST /api/transactions`**: Membuat transaksi baru.
    -   **Request Body**: `TransactionRequest` (detail lihat DTO)
    -   **Response**: `TransactionResponse`
    -   **Permissions**: ADMIN, STAFF
-   **`GET /api/transactions/{id}`**: Mendapatkan transaksi berdasarkan ID.
    -   **Response**: `TransactionResponse`
    -   **Permissions**: `isAuthenticated()` (detail otorisasi di service)
-   **`GET /api/transactions`**: Mendapatkan transaksi dengan filter dan paginasi.
    -   **Query Params**: `customerName`, `startDate` (ISO DateTime), `endDate` (ISO DateTime), `paymentStatus` (list), `staffId`, `sortByNewest` (boolean), `page`, `size`
    -   **Response**: Daftar `TransactionResponse`
    -   **Permissions**: `isAuthenticated()` (detail otorisasi di service)
-   **`PUT /api/transactions/{id}/status`**: Memperbarui status transaksi.
    -   **Query Param**: `status` (nilai dari enum `PaymentStatus`)
    -   **Response**: `TransactionResponse`
    -   **Permissions**: ADMIN, STAFF

### Laporan (`/api/reports`)

*Semua endpoint laporan memerlukan autentikasi.*

-   **`GET /api/reports/customer/spending/date-range`**: Mendapatkan total pengeluaran pelanggan berdasarkan rentang tanggal.
    -   **Query Params**: `customerId` (opsional, UUID), `startDate` (ISO Date), `endDate` (ISO Date)
    -   **Response**: `BigDecimal` (total pengeluaran)
-   **`GET /api/reports/customer/spending/all-time`**: Mendapatkan total pengeluaran pelanggan sepanjang waktu.
    -   **Query Params**: `customerId` (opsional, UUID)
    -   **Response**: `BigDecimal` (total pengeluaran)
-   **`GET /api/reports/customer/spending/date-range/pdf`**: Mengunduh laporan pengeluaran pelanggan (rentang tanggal) dalam format PDF.
    -   **Query Params**: `customerId` (opsional, UUID), `startDate` (ISO Date), `endDate` (ISO Date)
    -   **Response**: File PDF
-   **`GET /api/reports/customer/spending/all-time/pdf`**: Mengunduh laporan pengeluaran pelanggan (sepanjang waktu) dalam format PDF.
    -   **Query Params**: `customerId` (opsional, UUID)
    -   **Response**: File PDF
-   **`GET /api/reports/product/spending/all-time`**: (Dikomentari di kode) Mendapatkan total pengeluaran per produk sepanjang waktu.
    -   **Permissions**: ADMIN, STAFF
-   **`GET /api/reports/product/spending/all-time/pdf`**: (Dikomentari di kode) Mengunduh laporan pengeluaran per produk (sepanjang waktu) dalam format PDF.
    -   **Permissions**: ADMIN, STAFF

### Pajak (`/api/taxes`)

*Semua endpoint pajak memerlukan role ADMIN, kecuali GET yang juga bisa diakses STAFF.*

-   **`POST /api/taxes`**: (Dikomentari di kode) Membuat data pajak baru.
    -   **Permissions**: ADMIN
-   **`PUT /api/taxes/{id}`**: (Dikomentari di kode) Memperbarui data pajak berdasarkan ID.
    -   **Permissions**: ADMIN
-   **`GET /api/taxes/{id}`**: Mendapatkan data pajak berdasarkan ID.
    -   **Response**: `TaxResponse`
    -   **Permissions**: ADMIN, STAFF
-   **`GET /api/taxes`**: Mendapatkan semua data pajak.
    -   **Response**: Daftar `TaxResponse`
    -   **Permissions**: ADMIN, STAFF
-   **`DELETE /api/taxes/{id}`**: Menghapus data pajak berdasarkan ID.
    -   **Permissions**: ADMIN

### Pengguna (`/api/users`)

-   **`PUT /api/users/profile`**: Memperbarui profil pengguna yang sedang login.
    -   **Request Body**: `UpdateProfileRequest` (detail lihat DTO)
    -   **Response**: `UserResponse`
    -   **Permissions**: `hasAuthority()` (CUSTOMER, STAFF, ADMIN dapat update profil sendiri)
-   **`GET /api/users/{id}`**: Mendapatkan pengguna berdasarkan ID.
    -   **Response**: `UserResponse`
    -   **Permissions**: ADMIN
-   **`GET /api/users`**: Mendapatkan semua pengguna.
    -   **Response**: Daftar `UserResponse`
    -   **Permissions**: ADMIN
-   **`DELETE /api/users/{id}`**: Menghapus pengguna berdasarkan ID.
    -   **Permissions**: ADMIN

---
