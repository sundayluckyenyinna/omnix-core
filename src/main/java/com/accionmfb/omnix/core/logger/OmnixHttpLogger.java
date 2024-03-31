package com.accionmfb.omnix.core.logger;

import com.accionmfb.omnix.core.annotation.HttpLoggingAdvice;
import com.accionmfb.omnix.core.commons.LogPolicy;
import com.accionmfb.omnix.core.commons.LogStyle;
import com.accionmfb.omnix.core.commons.StringValues;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Method;
import java.util.*;


@Slf4j
@Configuration
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = { LoggerStyleProperties.class })
public class OmnixHttpLogger {

    private final LoggerStyleProperties loggerStyleProperties;
    private final ObjectMapper objectMapper;

    public void logHttpApiRequest(Object requestBody, HttpServletRequest servletRequest){
        try {
            System.out.println();
            log.info("=============================================  HTTP REQUEST START ======================================================");
            log.info("Request URI: {} {}", servletRequest.getMethod(), servletRequest.getRequestURI());
            log.info("Request SessionId: {}", servletRequest.getRequestedSessionId());
            writeBodyByLogStyle(requestBody);
            log.info("Request Headers: {}", getHeadersFromServletRequest(servletRequest));
            log.info("Request Params: {}", getTypesafeRequestMap(servletRequest));
            log.info("Request Protocol: {}", servletRequest.getProtocol());
            log.info("Request PathInfo: {}", servletRequest.getPathInfo());
            log.info("Request Context Path: {}", servletRequest.getContextPath());
            log.info("Request Scheme: {}", servletRequest.getScheme());
            log.info("Request Server Port: {}", servletRequest.getServerPort());
            log.info("Remote Address: {}", servletRequest.getRemoteAddr());
            log.info("Remote Host: {}", servletRequest.getRemoteHost());
            log.info("Remote Port: {}", servletRequest.getRemotePort());
            log.info("Local Name: {}", servletRequest.getLocalName());
            log.info("Local Address: {}", servletRequest.getLocalAddr());
            log.info("Local Port: {}", servletRequest.getLocalPort());
            log.info("=======================================================================================================================");
        }catch (Exception ignored){}
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

    public void logHttpApiResponse(Object responseBody, int status, HttpServletResponse servletResponse, Method method){
        if(shouldGoAheadWithResponseLogging(method)){
            logHttpApiResponse(responseBody, status, servletResponse);
        }
    }

    private void writeBodyByLogStyle(Object body) throws JsonProcessingException {
        if(getLogStyle(loggerStyleProperties.getLogStyle()) == LogStyle.PRETTY_PRINT) {
            log.info("Request Body: {}", Objects.isNull(body) ? StringValues.EMPTY_STRING : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
        }else{
            log.info("Request Body: {}", Objects.isNull(body) ? StringValues.EMPTY_STRING : (body instanceof String ? (String) body : objectMapper.writeValueAsString(body)));
        }
    }

    private Map<String, String> getHeadersFromServletRequest(HttpServletRequest servletRequest){
        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while(headerNames.hasMoreElements()){
            String header = headerNames.nextElement();
            headers.put(header, servletRequest.getHeader(header));
        }
        return headers;
    }

    private Map<String, String> getHeadersFromServletResponse(HttpServletResponse servletResponse){
        Collection<String> headerNames = servletResponse.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        for(String headerName : headerNames){
            headers.put(headerName, servletResponse.getHeader(headerName));
        }
        return headers;
    }

    private Map<String, String> getTypesafeRequestMap(HttpServletRequest request) {
        Map<String, String> typesafeRequestMap = new HashMap<>();
        Enumeration<?> requestParamNames = request.getParameterNames();
        while (requestParamNames.hasMoreElements()) {
            String requestParamName = (String) requestParamNames.nextElement();
            String requestParamValue;
            if (requestParamName.equalsIgnoreCase("password")) {
                requestParamValue = "********";
            } else {
                requestParamValue = request.getParameter(requestParamName);
            }
            typesafeRequestMap.put(requestParamName, requestParamValue);
        }
        return typesafeRequestMap;
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
        return Objects.nonNull(resolvedLogPolicy) && (resolvedLogPolicy == LogPolicy.REQUEST || resolvedLogPolicy == LogPolicy.REQUEST_AND_RESPONSE);
    }

    private boolean shouldGoAheadWithResponseLogging(Method method){
        LogPolicy resolvedLogPolicy = getResolvedLogPolicyRelaxation(method);
        return Objects.nonNull(resolvedLogPolicy) && (resolvedLogPolicy == LogPolicy.RESPONSE || resolvedLogPolicy == LogPolicy.REQUEST_AND_RESPONSE);
    }
}
