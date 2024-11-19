package us.reindeers.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import us.reindeers.common.constant.domain.ResultData;

@Slf4j
@RestControllerAdvice
@AllArgsConstructor
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    /**
     * jackson工具object mapper 会把object里面的key value写成json格式
     */
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 这个方法是扫描全局，对rest controller返回的数据进行预处理。
     * 添加sneaky throws是因为writeValueAsString方法需要抛出异常
     * @param body
     * @param returnType
     * @param selectedContentType
     * @param selectedConverterType
     * @param request
     * @param response
     * @return
     */

    @Override
    @SneakyThrows
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 因为springboot会直接返回string, 这里需要手动转成json string
        if(body instanceof String) {
            return objectMapper.writeValueAsString(ResultData.success(body));
        }


        //这里直接return 异常返回的body 不做额外处理
        if(body instanceof ResultData) return body;

        if(body instanceof byte[]) return body;

        return ResultData.success(body);
    }
}
