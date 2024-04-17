package com.accionmfb.omnix.core.logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public interface Loggable {

    default Map<String, String> getHeadersFromServletRequest(HttpServletRequest servletRequest){
        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while(headerNames.hasMoreElements()){
            String header = headerNames.nextElement();
            headers.put(header, servletRequest.getHeader(header));
        }
        return headers;
    }

    default Map<String, String> getHeadersFromServletResponse(HttpServletResponse servletResponse){
        Collection<String> headerNames = servletResponse.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        for(String headerName : headerNames){
            headers.put(headerName, servletResponse.getHeader(headerName));
        }
        return headers;
    }

    default Map<String, String> getTypesafeRequestMap(HttpServletRequest request) {
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

}
