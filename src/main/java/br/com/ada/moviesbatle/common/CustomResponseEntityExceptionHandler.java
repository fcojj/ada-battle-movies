package br.com.ada.moviesbatle.common;

import br.com.ada.moviesbatle.match.boundary.TokenException;
import br.com.ada.moviesbatle.quiz.boundary.QuizException;
import br.com.ada.moviesbatle.quiz.control.OMDBRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
@Slf4j
public class CustomResponseEntityExceptionHandler {

    @ExceptionHandler(OMDBRequestException.class)
    protected ResponseEntity<ErrorResponse> handleOMDBRequestException(OMDBRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Internal Error. Sorry, please try again.", List.of(ex.getMessage()));

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TokenException.class)
    protected ResponseEntity<ErrorResponse> handleAssignTokenToMatchException(TokenException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Verify token.", List.of(ex.getMessage()));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(QuizException.class)
    protected ResponseEntity<ErrorResponse> handleQuizException(QuizException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Invalid request", List.of(ex.getMessage()));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }





}