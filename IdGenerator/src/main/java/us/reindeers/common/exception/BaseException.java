package us.reindeers.common.exception;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import us.reindeers.common.constant.template.ReturnCode;

/**
 *  作为所有业务异常的base class
 */
@Data
@Builder
@NoArgsConstructor
public class BaseException extends RuntimeException {

    /**
     * 抛出异常时的错误码，用于前端展示
     */
    private ReturnCode returnCode;

    private String message;

    public BaseException(ReturnCode returnCode) {
        this.returnCode = returnCode;
    }

    public BaseException(ReturnCode returnCode, String message) {
        super(message);
        this.returnCode = returnCode;

    }
}
