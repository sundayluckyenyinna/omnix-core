package com.accionmfb.omnix.core.advice;

import com.accionmfb.omnix.core.annotation.IdTokenUnaware;
import com.accionmfb.omnix.core.annotation.ServiceOperation;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.service.DatasourceService;
import com.accionmfb.omnix.core.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestControllerIdTokenUnawareConfig {

    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${server.port}")
    private String port;
    private final DatasourceService datasourceService;

    @EventListener(ApplicationStartedEvent.class)
    @ServiceOperation(description = "Registers mappings that does not require ID-Token")
    public void initIdTokenUnawareMappingConfiguration(ApplicationStartedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlers = requestMappingHandlerMapping
                .getHandlerMethods();
        registerIdTokenUnawareMappingEntries(handlers);
    }

    @ServiceOperation(description = "Registers those mappings that are ID-Token unaware")
    private void registerIdTokenUnawareMappingEntries(Map<RequestMappingInfo, HandlerMethod> handlers){
        for(Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlers.entrySet()){
            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            updateNewIdTokenUnawareMappingEntrySet(mappingInfo, handlerMethod);
        }
    }

    @ServiceOperation(description = "Update the set of mappings that are not ID-Token aware")
    private void updateNewIdTokenUnawareMappingEntrySet(RequestMappingInfo mappingInfo, HandlerMethod handlerMethod){
        Method controllerMethod = handlerMethod.getMethod();
        String antPrefix = "/**";
        String combinedMethodPatterns = getCombinePattern(mappingInfo.getPatternsCondition().getPatterns());
        String completeAntPattern = antPrefix.concat(combinedMethodPatterns);
        IdTokenUnaware idTokenUnaware = controllerMethod.getDeclaredAnnotation(IdTokenUnaware.class);
        if(Objects.nonNull(idTokenUnaware)){
            if(!datasourceService.hasEndpoint(completeAntPattern)) {
                log.info("Adding new pattern '{}' that does not require ID-Token", completeAntPattern);
                datasourceService.saveIdTokenUnawareEndpoint(completeAntPattern, CommonUtil.returnOrDefault(applicationName, String.join(StringValues.COLON, List.of("localhost", port))));
            }
        }else{
            if(datasourceService.hasEndpoint(completeAntPattern)) {
                log.info("Removed pattern '{}' from ID-Token whitelist as it now requires ID-Token", completeAntPattern);
                datasourceService.deleteIdTokenUnawareEndpoint(completeAntPattern);
            }
        }
    }

    private static String getCombinePattern(Set<String> patterns){
        return String.join(StringValues.FORWARD_STROKE, patterns);
    }
}
