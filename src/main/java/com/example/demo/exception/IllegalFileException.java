package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED)
public class IllegalFileException extends RuntimeException{
    public IllegalFileException(String msg){
        super(msg);
    }
}

