package com.devon.building.exception;


import com.devon.building.model.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public ResponseEntity<Object> handleException(ArrayIndexOutOfBoundsException e){
        ResponseDTO errorResponse = new ResponseDTO();
        errorResponse.setMessage(e.getMessage());
        List<String> details = new ArrayList<>();
        details.add("b dang co truy cap 1 phan tu ngoai mang");
        errorResponse.setDetails(details);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Object> handleException(SQLException e){
        ResponseDTO errorResponse = new ResponseDTO();
        errorResponse.setMessage(e.getMessage());
        List<String> details = new ArrayList<>();
        details.add("KET NOI SQL K THANH CONG");
        errorResponse.setDetails(details);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<Object> handleException(InvalidDataException e){
        ResponseDTO errorResponse = new ResponseDTO();
        errorResponse.setMessage(e.getMessage());
        List<String> details = new ArrayList<>();
        details.add("DU LIEU LOI");
        errorResponse.setDetails(details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e){
        ResponseDTO errorResponse = new ResponseDTO();
        errorResponse.setMessage(e.getMessage());
        List<String> details = new ArrayList<>();
        details.add("LOI ROI");
        errorResponse.setDetails(details);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
