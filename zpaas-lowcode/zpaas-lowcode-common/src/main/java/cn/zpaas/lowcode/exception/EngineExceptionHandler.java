package cn.zpaas.lowcode.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.vo.ResponseResult;

/**
 * 统一异常处理类
 *
 * @author zjy
 * createTime 2025年4月21日-09:45:11
 */
@RestControllerAdvice
@ResponseBody
public class EngineExceptionHandler {
    @ExceptionHandler(EngineException.class)
    public ResponseResult<Object> exception(EngineException ex) {
        return ResponseResult.error(ex.getStatus(), ex.getMessage(), ex.getDetailMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult<Object> exception(Exception ex) {
        
        return ResponseResult.error(ResultStatus.BUSINESS_ERROR, ex.getMessage());
    }
}
