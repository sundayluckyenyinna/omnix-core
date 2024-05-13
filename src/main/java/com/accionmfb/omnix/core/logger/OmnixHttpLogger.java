package com.accionmfb.omnix.core.logger;

import com.accionmfb.omnix.core.annotation.HttpLoggingAdvice;
import com.accionmfb.omnix.core.commons.LogPolicy;
import com.accionmfb.omnix.core.commons.LogStyle;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.util.CommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;


@Slf4j
@Configuration

@RequiredArgsConstructor
@EnableConfigurationProperties(value = { LoggerStyleProperties.class })
public class OmnixHttpLogger implements Loggable{

    private final LoggerStyleProperties loggerStyleProperties;
    private final ObjectMapper objectMapper;

    public void logHttpApiRequest(Object requestBody, HttpServletRequest servletRequest){
        try {
            System.out.println();
            log.info("=============================================  HTTP REQUEST START ======================================================");
            log.info("Request URI: {} {}", servletRequest.getMethod(), servletRequest.getRequestURI());
            log.info("Remote IP: {}", CommonUtil.returnOrDefault(servletRequest.getHeader("X-FORWARDED-FOR"), servletRequest.getRemoteAddr()));
            log.info("Request SessionId: {}", servletRequest.getRequestedSessionId());
            writeBodyByLogStyle(requestBody);
            log.info("Request Headers: {}", getHeadersFromServletRequest(servletRequest));
            log.info("Request Params: {}", getTypesafeRequestMap(servletRequest));
            log.info("Request Protocol: {}", servletRequest.getProtocol());
            log.info("Request Scheme: {}", servletRequest.getScheme());
            log.info("-----------------------------------------------------------------------------------------------------------------------");
        } catch (Exception ignored) {
        }
    }

    public void logHttpApiRequest(Object requestBody, HttpServletRequest servletRequest, Method method){
        if(shouldGoAheadWithRequestLogging(method)) {
            logHttpApiRequest(requestBody, servletRequest);
        }
    }

    public void logHttpApiResponse(Object responseBody, HttpServletResponse servletResponse){
        try {
            System.out.println();
            log.info("=============================================  HTTP RESPONSE END ======================================================");
            log.info("Response Status: {}", HttpStatus.resolve(servletResponse.getStatus()));
            writeBodyByLogStyle(responseBody);
            log.info("Response Headers: {}", getHeadersFromServletResponse(servletResponse));
            log.info("=======================================================================================================================");
            System.out.println();
        }catch (Exception ignored){}
    }

    public void logHttpApiResponse(Object responseBody, HttpServletResponse servletResponse, Method method){
        if(shouldGoAheadWithResponseLogging(method)){
            logHttpApiResponse(responseBody, servletResponse);
        }
    }

    public void logHttpApiResponse(Object responseBody, int status, HttpServletResponse servletResponse){
        try {
            log.info("");
            log.info("=============================================  HTTP RESPONSE END ======================================================");
            log.info("Response Status: {}", HttpStatus.resolve(status));
            writeBodyByLogStyle(responseBody);
            log.info("Response Headers: {}", getHeadersFromServletResponse(servletResponse));
            log.info("=======================================================================================================================");
        }catch (Exception ignored){}
    }

    private void writeBodyByLogStyle(Object body) throws JsonProcessingException {
        if(getLogStyle(loggerStyleProperties.getLogStyle()) == LogStyle.PRETTY_PRINT) {
            log.info("Body: {}", Objects.isNull(body) ? StringValues.EMPTY_STRING : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
        }else{
            log.info("Body: {}", Objects.isNull(body) ? StringValues.EMPTY_STRING : (body instanceof String ? (String) body : objectMapper.writeValueAsString(body)));
        }
    }

    private LogStyle getLogStyle(String logStyle){
        try{
            return LogStyle.valueOf(logStyle.toUpperCase());
        }catch (Exception exception){
            return LogStyle.DEFAULT;
        }
    }

    private LogPolicy getResolvedLogPolicyRelaxation(Method controllerMethod){
        HttpLoggingAdvice methodAnn = null;
        HttpLoggingAdvice classAnn = null;
        if(Objects.nonNull(controllerMethod)){
            methodAnn = controllerMethod.getAnnotation(HttpLoggingAdvice.class);
            classAnn = controllerMethod.getDeclaringClass().getAnnotation(HttpLoggingAdvice.class);
        }

        LogPolicy finalDirectionToRelax = null;
        if(Objects.nonNull(methodAnn)){
            finalDirectionToRelax = methodAnn.direction();
        }
        else if(Objects.nonNull(classAnn)){
            finalDirectionToRelax = classAnn.direction();
        }
        return finalDirectionToRelax;
    }

    private boolean shouldGoAheadWithRequestLogging(Method method){
        LogPolicy resolvedLogPolicy = getResolvedLogPolicyRelaxation(method);
        return Objects.isNull(resolvedLogPolicy) || resolvedLogPolicy == LogPolicy.REQUEST || resolvedLogPolicy == LogPolicy.REQUEST_AND_RESPONSE;
    }

    private boolean shouldGoAheadWithResponseLogging(Method method){
        LogPolicy resolvedLogPolicy = getResolvedLogPolicyRelaxation(method);
        return Objects.isNull(resolvedLogPolicy) || resolvedLogPolicy == LogPolicy.RESPONSE || resolvedLogPolicy == LogPolicy.REQUEST_AND_RESPONSE;
    }
}
