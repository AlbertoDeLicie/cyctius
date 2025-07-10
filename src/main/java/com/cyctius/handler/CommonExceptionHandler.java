package com.cyctius.handler;

import com.cyctius.dto.ErrorResponse;
import com.cyctius.handler.exception.AlreadyExistException;
import com.cyctius.handler.exception.BadRequestException;
import com.cyctius.handler.exception.NotFoundException;
import com.cyctius.util.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public final class CommonExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageService messageService;

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notFoundExceptionHandler(
        final NotFoundException ex
    ) {
        val errorResponse = new ErrorResponse();
        errorResponse.setMessage(messageService.getMessage(ex.getMessage()));

        log.error(errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> alreadyExistExceptionHandler(
        final AlreadyExistException ex
    ) {
        val errorResponse = new ErrorResponse();
        errorResponse.setMessage(messageService.getMessage(ex.getMessage()));

        log.error(errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> badRequestExceptionHandler(
        final BadRequestException ex
    ) {
        val errorResponse = new ErrorResponse();
        errorResponse.setMessage(messageService.getMessage(ex.getMessage()));

        log.error(errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
