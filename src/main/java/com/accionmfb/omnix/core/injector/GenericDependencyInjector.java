package com.accionmfb.omnix.core.injector;

import com.accionmfb.omnix.core.annotation.FallbackParam;
import com.accionmfb.omnix.core.commons.StringValues;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class GenericDependencyInjector {

    public static Object[] resolveFallbackMethodArgumentsInjection(Method method, Parameter[] causingParameters, Object ... objects){
        List<Parameter> parameters = List.of(method.getParameters());
        Object[] params = new Object[parameters.size()];
        Arrays.fill(params, null);
        Map<String, Object> parameterValueMap = getParameterValueMap(causingParameters, objects);

        for(Parameter parameter : parameters) {
            int parameterIndex = parameters.indexOf(parameter);
            FallbackParam fallbackParam = parameter.getAnnotation(FallbackParam.class);
            if(Objects.nonNull(fallbackParam)){
                String fallbackParamValue = fallbackParam.value();
                if(Objects.nonNull(fallbackParamValue)){
                    String key = fallbackParamValue.trim().equalsIgnoreCase(StringValues.EMPTY_STRING) ? parameter.getName() : fallbackParamValue;
                    params[parameterIndex] = parameterValueMap.get(key);
                }else{
                    params[parameterIndex] = parameterValueMap.get(parameter.getName());
                }
            }else{
                params[parameterIndex] = parameterValueMap.get(parameter.getName());
            }
        }

        return params;
    }

    public static Map<String, Object> getParameterValueMap(Parameter[] parameters, Object ...objects){
        Map<String, Object> result = new HashMap<>();
        if(parameters.length != objects.length){
            throw new IllegalArgumentException("Parameter and value lengths mismatch");
        }
        for(int i = 0; i < parameters.length; i++){
            result.put(parameters[i].getName(), objects[i]);
        }
        return result;
    }
}
