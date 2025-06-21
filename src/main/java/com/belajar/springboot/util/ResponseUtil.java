package com.belajar.springboot.util;

import com.belajar.springboot.dto.response.CommonResponse;
import com.belajar.springboot.dto.response.PagingResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {


    private ResponseUtil() {

    }


    public static <T> ResponseEntity<CommonResponse<T>> build(HttpStatus httpStatus, String message, T data) {
        CommonResponse<T> response = CommonResponse.<T>builder()
                .statusCode(httpStatus.value())
                .message(message)
                .data(data)
                // paging akan null
                .build();
        return ResponseEntity.status(httpStatus).body(response);
    }


    public static <T> ResponseEntity<CommonResponse<T>> build(HttpStatus httpStatus, String message, T data, Page<?> page) {
        // Buat objek PagingResponse dari objek Page
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page.getNumber() + 1) // Page index dimulai dari 0, response biasa mulai dari 1
                .totalPages(page.getTotalPages())
                .totalElement(page.getTotalElements()) // Ganti nama field jika berbeda di DTO Anda
                .content(null)
                .build();

        // Bangun CommonResponse dengan data dan paging
        CommonResponse<T> response = CommonResponse.<T>builder()
                .statusCode(httpStatus.value())
                .message(message)
                .data(data) // Masukkan list data di sini
                .paging(pagingResponse) // Masukkan info paging
                .build();

        return ResponseEntity.status(httpStatus).body(response);
    }


    public static ResponseEntity<CommonResponse<Object>> buildError(HttpStatus httpStatus, String message) {
        CommonResponse<Object> response = CommonResponse.builder()
                .statusCode(httpStatus.value())
                .message(message)
                .data(null) // Tidak ada data untuk error sederhana
                .build();
        return ResponseEntity.status(httpStatus).body(response);
    }


    public static <E> ResponseEntity<CommonResponse<E>> buildError(HttpStatus httpStatus, String message, E errors) {
        CommonResponse<E> response = CommonResponse.<E>builder()
                .statusCode(httpStatus.value())
                .message(message)
                .data(errors) // Masukkan detail error (misal Map validasi)
                .build();
        return ResponseEntity.status(httpStatus).body(response);
    }
}