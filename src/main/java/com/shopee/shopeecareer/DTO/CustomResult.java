package com.shopee.shopeecareer.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResult {
    private int status;
    private String message;
    private Object data;
}
