package com.belajar.springboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// CommonResponse.java (tanpa @Builder, dengan constructor AllArgs)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonResponse<T> {
    private Integer statusCode;
    private String message;
    private T data;
    private PagingResponse paging;
}
