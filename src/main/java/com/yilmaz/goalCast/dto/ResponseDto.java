package com.yilmaz.goalCast.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto<T> {
    private String message;
    private boolean success;
    private T data;  // opsiyonel, gerekirse null olabilir
}
