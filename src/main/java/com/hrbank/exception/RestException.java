package com.hrbank.exception;

import lombok.Getter;

@Getter
public class RestException extends RuntimeException {
  private final ErrorCode errorCode;

  public RestException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}

