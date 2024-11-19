package us.reindeers.common.response.aspect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import us.reindeers.common.response.constant.domain.ResultData;
import us.reindeers.common.response.constant.template.ReturnCode;
import us.reindeers.common.response.exception.BaseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {
        log.info("GlobalExceptionHandler initialized");
    }
    /**
     * 处理自定义的 BaseException
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResultData<?>> handleBaseException(BaseException ex) {
        log.info("GlobalExceptionHandler initialized");

        HttpStatus status = switch (ex.getReturnCode()) {
            case RC400, DATABASE_ACCESS_ERROR, INVALID_INPUT, ID_OBTAINED_FAILURE,
                 ERROR_UPLOADING_AVATAR, INVALID_DONATION_STATUS, INVALID_REQUEST_STATUS -> HttpStatus.BAD_REQUEST;

            case USER_NOT_EXIST, DONOR_NOT_EXIST, GIFT_NOT_EXIST, REQUEST_NOT_EXIST,
                 NOTIFICATION_NOT_FOUND -> HttpStatus.NOT_FOUND;

            case RC500 -> HttpStatus.INTERNAL_SERVER_ERROR;

            case ROLE_NOT_SET, ROLE_NOT_EXIST, INVALID_USER, EMAIL_ALREADY_EXISTS,
                 ADDRESS_NOT_SUBMITTED -> HttpStatus.UNAUTHORIZED;

            case REQUEST_IS_FULFILLED -> HttpStatus.CONFLICT;

            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        // 使用 ResultData 构建响应体
        ResultData<?> result = ResultData.fail(ex.getReturnCode().getCode(), ex.getMessage());

        return new ResponseEntity<>(result, status);
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultData<?>> handleException(Exception ex) {
        // 记录异常日志
        log.error("Unhandled exception: ", ex);

        // 使用 ResultData 构建响应体
        ResultData<?> result = ResultData.fail(ReturnCode.RC500.getCode(), "Internal Server Error");

        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
