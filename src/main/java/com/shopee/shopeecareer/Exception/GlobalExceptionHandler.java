package com.shopee.shopeecareer.Exception;

import com.shopee.shopeecareer.DTO.CustomResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomResult> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CustomResult(400, ex.getMessage(), null));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomResult> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CustomResult(404, ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Lấy thông tin lỗi đầu tiên
        FieldError fieldError = ex.getBindingResult().getFieldError();

        // Kiểm tra xem có lỗi không và trả về thông báo tùy chỉnh
        if (fieldError != null) {
            String errorMessage = fieldError.getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomResult(400, errorMessage, null));
        }

        // Nếu không có lỗi, trả về thông báo mặc định
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CustomResult(400, "Validation failed", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResult> handleGlobalException(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomResult(500, "An error occurred", null));
    }
}




