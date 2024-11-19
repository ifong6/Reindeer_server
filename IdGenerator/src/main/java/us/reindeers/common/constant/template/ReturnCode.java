package us.reindeers.common.constant.template;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReturnCode {

    /**操作成功**/
    RC100(100,"success"),

    /**服务错误**/
    RC400(400, "service failed"),

    /**服务异常**/
    RC500(500,"system error, try again later");

    /**自定义状态码**/
    private final int code;
    /**自定义描述**/
    private final String message;


}
