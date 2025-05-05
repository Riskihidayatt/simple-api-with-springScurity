package com.enigma.live_code_springboot.constant;
public enum ResponseMessage {

    // AUTH
    LOGIN_SUCCESS("Login berhasil"),
    LOGIN_FAILED("Username atau password salah"),
    REGISTER_SUCCESS("Registrasi berhasil"),
    UNAUTHORIZED("Akses tidak diizinkan"),

    // USER
    USER_CREATED("User berhasil dibuat"),
    USER_UPDATED("User berhasil diperbarui"),
    USER_DELETED("User berhasil dihapus"),
    USER_NOT_FOUND("User tidak ditemukan"),

    // CUSTOMER
    CUSTOMER_CREATED("Customer berhasil didaftarkan"),
    CUSTOMER_UPDATED("Customer berhasil diperbarui"),
    CUSTOMER_DELETED("Customer berhasil dihapus"),
    CUSTOMER_NOT_FOUND("Customer tidak ditemukan"),

    // PRODUCT
    PRODUCT_CREATED("Produk berhasil ditambahkan"),
    PRODUCT_UPDATED("Produk berhasil diperbarui"),
    PRODUCT_DELETED("Produk berhasil dihapus"),
    PRODUCT_NOT_FOUND("Produk tidak ditemukan"),

    // TRANSACTION
    TRANSACTION_CREATED("Transaksi berhasil dibuat"),
    TRANSACTION_UPDATED("Transaksi berhasil diperbarui"),
    TRANSACTION_DELETED("Transaksi berhasil dihapus"),
    TRANSACTION_NOT_FOUND("Transaksi tidak ditemukan"),
    TRANSACTION_REPORT_SUCCESS("Laporan transaksi berhasil dibuat"),

    // GENERAL
    SUCCESS("Permintaan berhasil diproses"),
    FAILED("Terjadi kesalahan saat memproses permintaan"),
    DATA_NOT_FOUND("Data tidak ditemukan"),
    VALIDATION_FAILED("Validasi gagal");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}