package com.venturebridge.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleNotFound(ResourceNotFoundException exception, Model model, HttpServletRequest request) {
        log.error("Resource not found exception: ", exception);
        if (request.getRequestURI().startsWith("/api")) {
            Map<String, String> body = new HashMap<>();
            body.put("error", exception.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        model.addAttribute("error", exception.getMessage());
        return "error/404";
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public Object handleDuplicate(DuplicateResourceException exception, Model model, HttpServletRequest request) {
        log.error("Duplicate resource exception: ", exception);
        if (request.getRequestURI().startsWith("/api")) {
            Map<String, String> body = new HashMap<>();
            body.put("error", exception.getMessage());
            return new ResponseEntity<>(body, HttpStatus.CONFLICT);
        }
        model.addAttribute("error", exception.getMessage());
        return "error/500";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(IllegalArgumentException exception, Model model, HttpServletRequest request) {
        log.error("Illegal argument exception: ", exception);
        if (request.getRequestURI().startsWith("/api")) {
            Map<String, String> body = new HashMap<>();
            body.put("error", exception.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        model.addAttribute("error", exception.getMessage());
        return "error/500";
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadable(HttpMessageNotReadableException exception, Model model, HttpServletRequest request) {
        log.error("HTTP message not readable exception: ", exception);
        if (request.getRequestURI().startsWith("/api")) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Malformed JSON request body: " + exception.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        model.addAttribute("error", "Malformed request content: " + exception.getMessage());
        return "error/500";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException exception, Model model, HttpServletRequest request) {
        log.error("Access denied exception: ", exception);
        if (request.getRequestURI().startsWith("/api")) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Access denied: " + exception.getMessage());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        }
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception exception, Model model, HttpServletRequest request) {
        log.error("Unhandled system exception: ", exception);
        if (request.getRequestURI().startsWith("/api")) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "An internal error occurred: " + exception.getMessage());
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        model.addAttribute("error", "An unexpected error occurred: " + exception.getMessage());
        return "error/500";
    }
}