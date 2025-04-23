package com.hrbank.exception;


import com.hrbank.dto.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestException.class)
    public ResponseEntity<ErrorResponse> handleRestException(
        RestException ex, 
        WebRequest request
    ) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("RestException: {} (status: {})", 
            errorCode.getMessage(), 
            errorCode.getStatus()
        );

        String timestamp = OffsetDateTime.now()
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String details = request.getDescription(false).replace("uri=", "");

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ErrorResponse.of(errorCode, details, timestamp));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, WebRequest request) {
        log.error("Unexpected Exception: ", ex);

        String timestamp = OffsetDateTime.now()
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String details = request.getDescription(false).replace("uri=", "");

        ErrorResponse response = new ErrorResponse(
            "INTERNAL_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "서버 내부 오류가 발생했습니다.",
            timestamp,
            details
        );

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
}

