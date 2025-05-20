package com.example.task_manager.exception;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Test
    void handleValidationExceptions_shouldReturnErrorsMap() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        
        MethodArgumentNotValidException exception = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "errorMessage");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        
        // Act
        Map<String, String> errors = handler.handleValidationExceptions(exception);
        
        // Assert
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey("fieldName"));
        assertEquals("errorMessage", errors.get("fieldName"));
    }
} 