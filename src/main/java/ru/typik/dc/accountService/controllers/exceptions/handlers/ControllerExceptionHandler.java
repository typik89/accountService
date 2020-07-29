package ru.typik.dc.accountService.controllers.exceptions.handlers;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ru.typik.dc.accountService.controllers.exceptions.AccountNotFoundException;
import ru.typik.dc.accountService.controllers.exceptions.OverdraftException;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseBody
    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String accountNotFoundHandler(AccountNotFoundException exception) {
        return String.format("Account %s was not found", exception.getAccount());
    }

    @ResponseBody
    @ExceptionHandler(OverdraftException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public String overdraftHandler(OverdraftException exception) {
        return String.format("Overdraft for account %s and amount %s", exception.getAccount().getAccountId(),
                exception.getAmount().toString());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> illegalArgumentHandler(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getAllErrors().stream().map(el -> ((FieldError) el))
                .collect(Collectors.toMap(el -> el.getField(), el -> el.getDefaultMessage()));
    }

}
