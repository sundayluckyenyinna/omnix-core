package com.accionmfb.omnix.core.advice;

import com.accionmfb.omnix.core.commons.ResponseCodes;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.encryption.EncryptionProperties;
import com.accionmfb.omnix.core.encryption.manager.OmnixEncryptionService;
import com.accionmfb.omnix.core.exception.OmnixApiException;
import com.accionmfb.omnix.core.logger.OmnixHttpLogger;
import com.accionmfb.omnix.core.payload.ApiBaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AutoConfiguration
@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OmnixRestControllerAdvice {

    private final OmnixHttpLogger logger;
    private final ObjectMapper objectMapper;
    private final EncryptionProperties encryptionProperties;
    private final OmnixEncryptionService omnixEncryptionService;

    @ExceptionHandler(value = OmnixApiException.class)
    public void onOmnixApiException(OmnixApiException exception, HttpServletResponse servletResponse) throws Exception {
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(exception.getCode());
        apiBaseResponse.setResponseMessage(exception.getMessage());
        apiBaseResponse.setErrors(exception.getErrors());
        writeResponseToClient(apiBaseResponse, exception.getStatusCode(), servletResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public void onMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletResponse servletResponse) throws Exception {
        List<ObjectError> objectErrors = exception.getBindingResult().getAllErrors();
        List<String> errors = objectErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(ResponseCodes.FAILED_MODEL.getResponseCode());
        apiBaseResponse.setResponseMessage(String.join(StringValues.COMMA, errors));
        apiBaseResponse.setErrors(Collections.singletonList(apiBaseResponse.getResponseMessage()));
        writeResponseToClient(apiBaseResponse, HttpStatus.BAD_REQUEST.value(), servletResponse);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public void onConstraintViolationException(ConstraintViolationException exception, HttpServletResponse servletResponse) throws Exception {
        List<String> errors = exception.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(ResponseCodes.FAILED_MODEL.getResponseCode());
        apiBaseResponse.setResponseMessage(String.join(StringValues.COMMA, errors));
        apiBaseResponse.setErrors(Collections.singletonList(apiBaseResponse.getResponseMessage()));
        writeResponseToClient(apiBaseResponse, HttpStatus.BAD_REQUEST.value(), servletResponse);
    }

    @ExceptionHandler(value = ValidationException.class)
    public void onValidationException(ValidationException exception, HttpServletResponse servletResponse) throws Exception {
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(ResponseCodes.FAILED_MODEL.getResponseCode());
        apiBaseResponse.setResponseMessage(exception.getMessage());
        apiBaseResponse.setErrors(Collections.singletonList(apiBaseResponse.getResponseMessage()));
        writeResponseToClient(apiBaseResponse, HttpStatus.BAD_REQUEST.value(), servletResponse);
    }

    @ExceptionHandler(value = ResponseStatusException.class)
    public void onResponseStatusException(ResponseStatusException exception, HttpServletResponse servletResponse) throws Exception {
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(ResponseCodes.HTTP_ERROR.getResponseCode());
        apiBaseResponse.setResponseMessage(exception.getMessage());
        apiBaseResponse.setErrors(Collections.singletonList(apiBaseResponse.getResponseMessage()));
        writeResponseToClient(apiBaseResponse, exception.getStatusCode().value(), servletResponse);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public void onMissingServletRequestParameterException(MissingServletRequestParameterException exception, HttpServletResponse servletResponse) throws Exception {
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(ResponseCodes.FAILED_MODEL.getResponseCode());
        apiBaseResponse.setResponseMessage(exception.getMessage());
        apiBaseResponse.setErrors(Collections.singletonList(apiBaseResponse.getResponseMessage()));
        writeResponseToClient(apiBaseResponse, HttpStatus.BAD_REQUEST.value(), servletResponse);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public void onHttpMessageNotReadableException(HttpMessageNotReadableException exception, HttpServletResponse servletResponse) throws Exception {
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(ResponseCodes.FAILED_MODEL.getResponseCode());
        apiBaseResponse.setResponseMessage(exception.getMessage());
        apiBaseResponse.setErrors(Collections.singletonList(apiBaseResponse.getResponseMessage()));
        writeResponseToClient(apiBaseResponse, HttpStatus.BAD_REQUEST.value(), servletResponse);
    }

    private void writeResponseToClient(Object payload, int statusCode, HttpServletResponse servletResponse) throws Exception{
        logger.logHttpApiResponse(payload, statusCode, servletResponse);
        servletResponse.setStatus(statusCode);
        servletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        servletResponse.setHeader("X-FORWARDED-FOR", "ACCION-MICROFINANCE-BANK");
        String responseJson = objectMapper.writeValueAsString(payload);
        if(encryptionProperties.isEnableEncryption()){
            responseJson = omnixEncryptionService.encrypt(responseJson);
        }
        servletResponse.getWriter().write(responseJson);
    }
}
