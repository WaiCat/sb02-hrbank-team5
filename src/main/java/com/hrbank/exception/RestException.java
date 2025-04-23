package com.hrbank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestException extends RuntimeException {
  private final ErrorCode errorCode;

  public RestException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}

