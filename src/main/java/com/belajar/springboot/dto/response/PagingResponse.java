package com.belajar.springboot.dto.response;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingResponse<T> {
    private Integer currentPage;
    private Integer totalPages;
   private Long totalElement;
   private List<T> content;
}
