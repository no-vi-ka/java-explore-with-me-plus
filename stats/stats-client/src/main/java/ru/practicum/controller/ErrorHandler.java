//package ru.practicum.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.util.Arrays;
//
//@RestControllerAdvice
//public class ErrorHandler {
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleThrowable(final Throwable e) {
//        return new ErrorResponse("Произошла непредвиденная ошибка", e.getMessage() + Arrays.toString(e.getStackTrace()));
//    }
//}
