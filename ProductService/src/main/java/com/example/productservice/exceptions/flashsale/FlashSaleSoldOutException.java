package com.example.productservice.exceptions.flashsale;

public class FlashSaleSoldOutException extends RuntimeException {

    public FlashSaleSoldOutException() {
        super("Flash sale sold out");
    }

}
