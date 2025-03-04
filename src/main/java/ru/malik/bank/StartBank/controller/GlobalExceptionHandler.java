package ru.malik.bank.StartBank.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.malik.bank.StartBank.exception.UserAlreadyExistsException;
import ru.malik.bank.StartBank.exception.UserErrorResponse;
import ru.malik.bank.StartBank.exception.UserNotCreatedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    //Не удалось создать пользователя
    @ExceptionHandler(UserNotCreatedException.class)
    private ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    // Возвращаем ошибку с кодом 400 (Bad Request) и сообщением, если не нашли пользователя
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<UserErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
