package io.wdd.common.handler;

import com.google.common.collect.Maps;
import io.wdd.common.beans.response.R;
import io.wdd.common.beans.response.ResultStat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

/**
 * 自定义  全局异常处理类
 * 全局处理响应数据的全局处理类，继承ResponseBodyAdvice接口重写其中的方法，
 * （带有@RequestMapping注解的方法上抛出的异常都会拦截），在此统一处理并统一返回数据格式
 */
@RestControllerAdvice
@Slf4j(topic = "Global Exception")
public class GlobalExceptionHandler {

    @Autowired
    private MyMessageSource messageSource;

    /**
     * 全局异常拦截方法
     * 这里拦截此异常，将异常中的信息提取返回有效信息！
     *
     * @param e SpringBoot参数校验(valid)过程中，检验失败会产生此异常，在此处拦截
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Object> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {

        // 从异常对象中拿到ObjectError对象，获取所有的错误信息
        List<ObjectError> objectError = e.getBindingResult().getAllErrors();

        // 然后提取错误提示信息进行返回
        HashMap<Integer, String> errorMap = new HashMap<>();
        objectError.forEach(objectError1 -> errorMap.put(objectError.indexOf(objectError1), objectError1.getDefaultMessage()));

        // 使用标准化返回体返回数据
        return R.resetResult(ResultStat.VALIDATE_FAILED.getCode(), ResultStat.VALIDATE_FAILED.getDescription(), errorMap);
        //return errorMap;
    }

    /**
     * @param exception 参数类型错误，拦截器
     * @return
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public Object methodArgumentNotValidException(MethodArgumentTypeMismatchException exception) {
        //按需重新封装需要返回的错误信息
        Map<String, String> invalidMap = new LinkedHashMap(99);
        //解析原错误信息，封装后返回，此处返回非法的字段名称，原始值，错误信息
        invalidMap.put(exception.getParameter().getParameterName(), ResultStat.PARAM_ERROR.getDescription());
        R<Map<String, String>> vo = new R<>();

        vo.setCode(ResultStat.PARAM_ERROR.getCode());
        vo.setMsg(getLocaleMsg(ResultStat.PARAM_ERROR.getDescription()));
        vo.setData(invalidMap);

        log.debug(exception.getMessage(), exception);

        return vo;
    }

    /**
     * 拦截数据库异常
     *
     * @param exception 数据库异常
     * @return
     */
    @ExceptionHandler(value = {SQLException.class, DuplicateKeyException.class})
    public R<String> sqlExceptionHandler(SQLException exception) {

        R<String> vo = new R<>();

        vo.setCode(ResultStat.PARAM_ERROR.getCode());
        vo.setMsg(getLocaleMsg(ResultStat.PARAM_ERROR.getDescription()));
        if (exception instanceof SQLIntegrityConstraintViolationException) {
            vo.setData("Data already exsit ! 操作失败！");
        } else {
            vo.setData("数据库异常，操作失败！");
        }

        log.debug(exception.getMessage(), exception);

        return vo;
    }

    @ExceptionHandler(MyRuntimeException.class)
    public R<Object> interceptMyRuntimeE(MyRuntimeException exception) {

        R<Object> R = new R<>();
        ResultStat status = exception.getStatus();
        if (status != null) {
            R.setMsg(getLocaleMsg(exception.getMessage(), exception.getParams()));
            R.setCode(status.getCode());
            R.setData(exception.getData());
        } else {
            R.setCode(ResultStat.FAILED.getCode());
            R.setMsg(getLocaleMsg(exception.getMessage(), exception.getParams()));
            R.setData(null);
        }
        return R;
    }
//    /**
//     *
//     * 统一Spring Security的认证错误
//     * */
//    @ExceptionHandler(value = BadCredentialsException.class)
//    public R<Object> badCredentialsException(BadCredentialsException exception){
//
//        log.error(exception.getDescription());
//
//        return R.failed(ResultStat.USER_AUTH_FAILED);
//    }

    /**
     * validate 验证错误handle
     */
    @ExceptionHandler(value = BindException.class)
    public Object bindExceptionHandler(BindException exception) {
        //按需重新封装需要返回的错误信息
        Map<String, String> invalidMap = Maps.newLinkedHashMap();

        //解析原错误信息，封装后返回，此处返回非法的字段名称，原始值，错误信息
        if (exception != null) {
            List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
            fieldErrors.stream().sorted(Comparator.comparing(FieldError::getCode)).forEach(error -> {
                String defaultMessage = error.getDefaultMessage();
                String finalMessage = defaultMessage;
                if (defaultMessage != null && defaultMessage.startsWith("{") && defaultMessage.endsWith("}")) {
                    finalMessage = messageSource.getMessage(defaultMessage.substring(1, defaultMessage.length() - 1));
                }

                if (StringUtils.isNotEmpty(invalidMap.get(error.getField()))) {
                    invalidMap.put(error.getField(),
                            invalidMap.get(error.getField()) + "," + finalMessage);
                } else {
                    invalidMap.put(error.getField(), finalMessage);
                }
            });
        }

        R<Map<String, String>> vo = new R<>();
        vo.setCode(ResultStat.VALIDATE_FAILED.getCode());
        vo.setMsg(getLocaleMsg(ResultStat.VALIDATE_FAILED.getDescription()));
        vo.setData(invalidMap);

        log.debug(exception.getMessage(), exception);

        return vo;

    }

    /**
     * 默认异常统一处理 Exception
     */
    @ExceptionHandler(value = Exception.class)
    public Object exceptionHandel(Exception e) {
        return getResponseVO(e, getLocaleMsg("系统错误，请联系管理员"));
    }

    private Object getResponseVO(Exception e, String msg) {
        R<String> R = new R<>();
        R.setCode(ResultStat.FAILED.getCode());
        R.setMsg(msg);
        R.setMsg(e.getMessage());
        log.error(e.getMessage(), e);
        return R;
    }

    private String getLocaleMsg(String msgCode, Object... params) {
        return messageSource.getMessage(msgCode, params);
    }

}

