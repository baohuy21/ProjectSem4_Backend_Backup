package com.shopee.shopeecareer.Exception;

import java.io.IOException;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
