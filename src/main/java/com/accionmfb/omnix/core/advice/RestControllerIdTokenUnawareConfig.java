package com.accionmfb.omnix.core.advice;

import com.accionmfb.omnix.core.annotation.IdTokenUnaware;
import com.accionmfb.omnix.core.annotation.ServiceOperation;
import com.accionmfb.omnix.core.commons.OmnixParam;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.localsource.core.LocalParamStorage;
import com.accionmfb.omnix.core.service.DatasourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestControllerIdTokenUnawareConfig {

    private final DatasourceService datasourceService;
    private final LocalParamStorage localParamStorage;

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
        String configuredIdTokenUnawareMappingSet = localParamStorage.getParamValueOrDefault(OmnixParam.ID_TOKEN_UNAWARE_WHITELIST, StringValues.EMPTY_STRING);
        for(Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlers.entrySet()){
            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            updateNewIdTokenUnawareMappingEntrySet(mappingInfo, handlerMethod, configuredIdTokenUnawareMappingSet);
        }
    }

    @ServiceOperation(description = "Update the set of mappings that are not ID-Token aware")
    private void updateNewIdTokenUnawareMappingEntrySet(RequestMappingInfo mappingInfo, HandlerMethod handlerMethod, String configuredIdTokenUnawareMappingSet){
        Method controllerMethod = handlerMethod.getMethod();
        String antPrefix = "/**";
        String combinedMethodPatterns = getCombinePattern(mappingInfo.getPatternsCondition().getPatterns());
        String completeAntPattern = antPrefix.concat(combinedMethodPatterns);
        IdTokenUnaware idTokenUnaware = controllerMethod.getDeclaredAnnotation(IdTokenUnaware.class);
        if(Objects.nonNull(idTokenUnaware)){
            if(!configuredIdTokenUnawareMappingSet.contains(completeAntPattern)) {
                configuredIdTokenUnawareMappingSet = configuredIdTokenUnawareMappingSet.concat(StringValues.COMMA).concat(completeAntPattern);
                log.info("Adding new pattern '{}' that does not require ID-Token", completeAntPattern);
                updateConfiguredIdTokenUnawareMappingSet(configuredIdTokenUnawareMappingSet);
            }
        }else{
            if(configuredIdTokenUnawareMappingSet.contains(completeAntPattern)) {
                configuredIdTokenUnawareMappingSet = configuredIdTokenUnawareMappingSet.replace(completeAntPattern, StringValues.EMPTY_STRING);
                log.info("Removed pattern '{}' from ID-Token whitelist as it now requires ID-Token", completeAntPattern);
                updateConfiguredIdTokenUnawareMappingSet(configuredIdTokenUnawareMappingSet);
            }
        }
    }

    private void updateConfiguredIdTokenUnawareMappingSet(String configuredIdTokenUnawareMappingSet){
        if(configuredIdTokenUnawareMappingSet.startsWith(StringValues.COMMA)) {
            configuredIdTokenUnawareMappingSet = configuredIdTokenUnawareMappingSet.replaceFirst(StringValues.COMMA, StringValues.EMPTY_STRING).trim();
        }
        configuredIdTokenUnawareMappingSet = configuredIdTokenUnawareMappingSet.replaceAll(",{2,}", StringValues.COMMA);
        boolean isUpdated  = datasourceService.updateOmnixParam(OmnixParam.ID_TOKEN_UNAWARE_WHITELIST, configuredIdTokenUnawareMappingSet);
        if(isUpdated){
            log.info("Omnix generic param with key: '{}' updated successfully", OmnixParam.ID_TOKEN_UNAWARE_WHITELIST);
        }else{
            log.warn("Could not update omnix generic param for key: '{}'", OmnixParam.ID_TOKEN_UNAWARE_WHITELIST);
        }
    }

    private static String getCombinePattern(Set<String> patterns){
        return String.join(StringValues.FORWARD_STROKE, patterns);
    }
}
