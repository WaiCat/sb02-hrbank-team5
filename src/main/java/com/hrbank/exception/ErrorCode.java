package com.hrbank.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "부서를 찾을 수 없습니다."),
  PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "프로필 이미지를 찾을 수 없습니다."),
  EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND, "직원을 찾을 수 없습니다."),

  EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
  FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),

  // BACKUP
  BACKUP_NOT_FOUND(HttpStatus.NOT_FOUND, "백업을 찾을 수 없습니다."),
  BACKUP_LATEST_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 상태에 해당하는 백업이 존재하지 않습니다."),
  BACKUP_CSV_NOT_FOUND(HttpStatus.NOT_FOUND, "백업 CSV 파일을 찾을 수 없습니다."),
  BACKUP_ERROR_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "백업 에러 로그 파일을 찾을 수 없습니다."),
  BACKUP_ALREADY_IN_PROGRESS(HttpStatus.CONFLICT, "이미 진행 중인 백업이 있습니다."),
  DEPARTMENT_NAME_DUPLICATED(HttpStatus.CONFLICT, "부서 이름이 이미 존재합니다."),
  DEPARTMENT_HAS_EMPLOYEES(HttpStatus.BAD_REQUEST, "소속된 직원이 있는 부서는 삭제할 수 없습니다."),

  STORAGE_INITIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"저장소 초기화 중 오류가 발생했습니다."),
  FILE_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 읽기 중 오류가 발생했습니다."),
  FILE_WRITE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패하였습니다"),
  FILE_ALREADY_EXIST(HttpStatus.CONFLICT, "파일이 이미 존재합니다."),
  FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일이 존재하지 않습니다."),
  FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 중 오류가 발생했습니다."),

  CHANGE_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 변경 이력이 없습니다."),
  INVALID_CURSOR(HttpStatus.BAD_REQUEST, "커서 값이 올바르지 않습니다."),
  INVALID_CHANGE_LOG_DATA(HttpStatus.BAD_REQUEST, "변경 로그 데이터가 올바르지 않습니다."),
  INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "시작일은 종료일보다 이후일 수 없습니다.");



  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }
}