package com.example.productservice.exceptions;

import com.example.productservice.entity.Category;

public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException(String message){
        super(message);
    }
}
