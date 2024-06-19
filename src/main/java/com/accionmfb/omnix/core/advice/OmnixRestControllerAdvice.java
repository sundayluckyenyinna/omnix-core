package com.accionmfb.omnix.core.advice;

import com.accionmfb.omnix.core.commons.ResponseCode;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.encryption.EncryptionProperties;
import com.accionmfb.omnix.core.encryption.manager.OmnixEncryptionService;
import com.accionmfb.omnix.core.exception.OmnixApiException;
import com.accionmfb.omnix.core.logger.OmnixHttpLogger;
import com.accionmfb.omnix.core.payload.ApiBaseResponse;
import com.accionmfb.omnix.core.payload.EncryptionPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.accionmfb.omnix")
public class OmnixRestControllerAdvice {

    private final OmnixHttpLogger logger;
    private final ObjectMapper objectMapper;
    private final EncryptionProperties encryptionProperties;
    private final OmnixEncryptionService omnixEncryptionService;

    @ExceptionHandler(value = OmnixApiException.class)
    public void onOmnixApiException(OmnixApiException exception, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception {
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(exception.getCode());
        apiBaseResponse.setResponseMessage(exception.getMessage());
        apiBaseResponse.setErrors(exception.getErrors());
        writeResponseToClient(apiBaseResponse, exception.getStatusCode(), servletRequest, servletResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public void onMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception {
        List<ObjectError> objectErrors = exception.getBindingResult().getAllErrors();
        List<String> errors = objectErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(ResponseCode.FAILED_MODEL);
        apiBaseResponse.setResponseMessage(String.join(StringValues.COMMA, errors));
        apiBaseResponse.setErrors(Collections.singletonList(apiBaseResponse.getResponseMessage()));
        writeResponseToClient(apiBaseResponse, HttpStatus.BAD_REQUEST.value(), servletRequest, servletResponse);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public void onConstraintViolationException(ConstraintViolationException exception, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception {
        List<String> errors = exception.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(ResponseCode.FAILED_MODEL);
        apiBaseResponse.setResponseMessage(String.join(StringValues.COMMA, errors));
        apiBaseResponse.setErrors(Collections.singletonList(apiBaseResponse.getResponseMessage()));
        writeResponseToClient(apiBaseResponse, HttpStatus.BAD_REQUEST.value(), servletRequest, servletResponse);
    }

    @ExceptionHandler(value = ValidationException.class)
    public void onValidationException(ValidationException exception, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception {
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(ResponseCode.FAILED_MODEL);
        apiBaseResponse.setResponseMessage(exception.getMessage());
        apiBaseResponse.setErrors(Collections.singletonList(apiBaseResponse.getResponseMessage()));
        writeResponseToClient(apiBaseResponse, HttpStatus.BAD_REQUEST.value(), servletRequest, servletResponse);
    }

    @ExceptionHandler(value = ResponseStatusException.class)
    public void onResponseStatusException(ResponseStatusException exception, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception {
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(ResponseCode.HTTP_ERROR);
        apiBaseResponse.setResponseMessage(exception.getMessage());
        apiBaseResponse.setErrors(Collections.singletonList(apiBaseResponse.getResponseMessage()));
        writeResponseToClient(apiBaseResponse, exception.getStatus().value(), servletRequest, servletResponse);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public void onMissingServletRequestParameterException(MissingServletRequestParameterException exception, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception {
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(ResponseCode.FAILED_MODEL);
        apiBaseResponse.setResponseMessage(exception.getMessage());
        apiBaseResponse.setErrors(Collections.singletonList(apiBaseResponse.getResponseMessage()));
        writeResponseToClient(apiBaseResponse, HttpStatus.BAD_REQUEST.value(), servletRequest, servletResponse);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public void onHttpMessageNotReadableException(HttpMessageNotReadableException exception, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception {
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(ResponseCode.FAILED_MODEL);
        apiBaseResponse.setResponseMessage(exception.getMessage());
        apiBaseResponse.setErrors(Collections.singletonList(apiBaseResponse.getResponseMessage()));
        writeResponseToClient(apiBaseResponse, HttpStatus.BAD_REQUEST.value(), servletRequest, servletResponse);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public void onGenericRuntimeException(RuntimeException exception, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception {
        ApiBaseResponse apiBaseResponse = new ApiBaseResponse();
        apiBaseResponse.setResponseCode(ResponseCode.FAILED_MODEL);
        apiBaseResponse.setResponseMessage(exception.getMessage());
        apiBaseResponse.setErrors(Collections.singletonList(apiBaseResponse.getResponseMessage()));
        writeResponseToClient(apiBaseResponse, HttpStatus.OK.value(), servletRequest, servletResponse);
    }

    private void writeResponseToClient(Object payload, int statusCode, HttpServletRequest servletRequest,  HttpServletResponse servletResponse) throws Exception{
        logger.logHttpApiResponse(payload, statusCode, servletResponse);
        servletResponse.setStatus(statusCode);
        servletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        servletResponse.setHeader("X-FORWARDED-FOR", "ACCION-MICROFINANCE-BANK");
        servletResponse.setHeader("Access-Control-Allow-Origin", "*");
        servletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        servletResponse.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, idToken");
        servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        servletResponse.setHeader("Access-Control-Max-Age", "31536000");
        String responseJson = objectMapper.writeValueAsString(payload);

        String encryptionKey = (String) servletRequest.getAttribute(StringValues.ENC_KEY_PLACEHOLDER);
        Boolean encryptionRequired = (Boolean) servletRequest.getAttribute(StringValues.APP_USER_REQUIRE_ENCY_KEY);
        if(encryptionProperties.isEnableEncryption() && Objects.nonNull(encryptionKey) && encryptionRequired){
            String encryptedResponse = omnixEncryptionService.encryptWithKey(responseJson, encryptionKey);
            EncryptionPayload encryptionPayload = EncryptionPayload.withResponse(encryptedResponse);
            responseJson = objectMapper.writeValueAsString(encryptionPayload);
        }
        log.info("Encrypted Exception Response: {}", responseJson);
        servletResponse.getWriter().write(responseJson);
    }
}
