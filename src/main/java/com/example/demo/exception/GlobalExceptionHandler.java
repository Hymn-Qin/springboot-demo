package com.example.demo.exception;

import com.example.demo.data.model.Result;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shuang.kou
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = UsernameAlreadyExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleUserNameAlreadyExistException(UsernameAlreadyExistException e) {
        logger.error("UsernameAlreadyExistException:{}", e.getMessage());
        return exceptionResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleUsernameNotFoundException(UsernameNotFoundException e) {

        return exceptionResponse(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(value = SQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleSQLException(SQLException e) {

        return exceptionResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = SignatureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleSignatureException(SignatureException e) {

        return exceptionResponse(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }

    /**
     * 系统异常处理，比如：404,500
     *
     * @param req
     * @param res
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object defaultErrorHandler(HttpServletResponse res, Exception e) {

        if (e instanceof org.springframework.web.servlet.NoHandlerFoundException) {
            return exceptionResponse(HttpServletResponse.SC_NOT_FOUND,"请检查请求路径或者类型是否正确");
        }
        return exceptionResponse(res.getStatus(), e.getMessage());
    }

    /**
     * 参数校验报错
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
        logger.error("MethodArgumentNotValidException:{}", errorMsg);
        return exceptionResponse(HttpServletResponse.SC_BAD_REQUEST, errorMsg);
    }

//    /**
//     * 统一处理请求参数校验(普通传参)
//     *
//     * @param e ConstraintViolationException
//     * @return FebsResponse
//     */
//    @ExceptionHandler(value = ConstraintViolationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public String handleConstraintViolationException(ConstraintViolationException e) {
//        StringBuilder message = new StringBuilder();
//        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
//        for (ConstraintViolation<?> violation : violations) {
//            Path path = violation.getPropertyPath();
//            String[] pathArr = StringUtils.split(path.toString(), ".");
//            message.append(pathArr[1]).append(violation.getMessage()).append(",");
//        }
//        message = new StringBuilder(message.substring(0, message.length() - 1));
//        return message.toString();
//    }

    private Object exceptionResponse(int code, String message) {
        return new Result.Failure(code, message);
    }
}
