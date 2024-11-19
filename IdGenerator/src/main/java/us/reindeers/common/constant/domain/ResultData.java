package us.reindeers.common.constant.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import us.reindeers.common.constant.template.ReturnCode;
import us.reindeers.common.exception.BaseException;

/**
 * 后端返回给前端的结果封装类
 * @param <T> 返回的数据类型
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ResultData<T> {

    //结果状态码
    private int status;
    private String message;
    private T data;
    private long timestamp = System.currentTimeMillis();

    public ResultData(ReturnCode returnCode) {
        this.status = returnCode.getCode();
        this.message = returnCode.getMessage();
    }

    /**
     * 请求成功时调用
     * @param data response中data部分
     * @return 请求成功
     * @param <T>
     */
    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(ReturnCode.RC100.getCode());
        resultData.setMessage(ReturnCode.RC100.getMessage());
        resultData.setData(data);

        return  resultData;
    }

    /**
     * 请求成功时调用(无参数)
     * @return 请求成功
     */
    public static ResultData success(){
        ResultData resultData = new ResultData();
        resultData.setStatus(ReturnCode.RC100.getCode());
        resultData.setMessage(ReturnCode.RC100.getMessage());

        return resultData;
    }

    public static <T> ResultData<T> fail(int code, String message) {
        return ResultData.<T>builder()
                .status(code)
                .message(message)
                .build();
    }

    public static <T> ResultData<T> fail(Exception e) {
        return ResultData.<T>builder()
                .status(ReturnCode.RC500.getCode())
                .message(e.getMessage())
                .build();
    }

    public static <T> ResultData<T> fail(BaseException e) {
        return ResultData.<T>builder()
                .status(e.getReturnCode().getCode())
                .message(e.getReturnCode().getMessage())
                .build();
    }
}
