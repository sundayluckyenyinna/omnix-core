package com.accionmfb.omnix.core.aop;

import com.accionmfb.omnix.core.annotation.FallbackAdvice;
import com.accionmfb.omnix.core.annotation.FallbackHandler;
import com.accionmfb.omnix.core.annotation.RelaxAspectLogOperation;
import com.accionmfb.omnix.core.commons.StringValues;
import com.accionmfb.omnix.core.injector.GenericDependencyInjector;
import com.accionmfb.omnix.core.util.OmnixCoreApplicationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Aspect
@Configuration
@AutoConfiguration
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class OmnixCoreMethodProxyInterceptor {

    private final ApplicationContext applicationContext;

    @Pointcut("@annotation(com.accionmfb.omnix.core.annotation.FallbackHandler)")
    public void methodWithFallbackHandlerInClassWithFallbackAdvice(){}

    @Before(value = "methodWithFallbackHandlerInClassWithFallbackAdvice()")
    public void beforeMethodWithFallbackHandlerInClassWithFallbackAdvice(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();
        Method method = getMethodFromSignature(joinPoint.getSignature());
        if(shouldLogAspectOperation(method, method.getDeclaringClass())) {
            log.info("----------------------------------- Before {} invocation -----------------------------------------------------", methodName);
            log.info("Omnix is starting method invocation to know if proxy interception is to be applied.");
            log.info("Method Name: {}", methodName);
            log.info("Method argument: {}", List.of(methodArgs));
            log.info("--------------------------------------------------------------------------------------------------------------");
        }
    }

    @AfterReturning(pointcut = "methodWithFallbackHandlerInClassWithFallbackAdvice()", returning = "returnValue")
    public void afterMethodReturning(JoinPoint joinPoint, Object returnValue){
        String methodName = joinPoint.getSignature().getName();
        Method method = getMethodFromSignature(joinPoint.getSignature());
        List<String> blackListedValues = new ArrayList<>();
        String fallbackMethodName = null;
        String className = null;
        if(Objects.nonNull(method)){
            FallbackHandler handler = method.getAnnotation(FallbackHandler.class);
            blackListedValues = Arrays.asList(handler.onValue());
            fallbackMethodName = handler.methodName();
            FallbackAdvice fallbackAdvice = method.getDeclaringClass().getAnnotation(FallbackAdvice.class);
            if(Objects.nonNull(fallbackAdvice)){
                className = fallbackAdvice.value().getName();
            }
        }

        String deduction;
        boolean willCallFallbackMethod = false;
        if(blackListedValues.contains(StringValues.EMPTY_STRING + returnValue)){
            deduction = String.format("Return value '%s' is found in the blacklisted values!", returnValue);
            willCallFallbackMethod = true;
        }else{
            deduction = String.format("Return value '%s' was NOT found in the blacklisted values!", returnValue);
        }
        if(shouldLogAspectOperation(method, method.getDeclaringClass())) {
            log.info("------------------------------------- After {} invocation ------------------------------------------------------", methodName);
            log.info("Method Name: {}", methodName);
            log.info("Return Value: {}", Objects.isNull(returnValue) ? "null" : returnValue);
            log.info("BlackListed values: {}", blackListedValues);
            log.info("Deduction: {}", deduction);
            if (Objects.nonNull(fallbackMethodName) && Objects.nonNull(className) && willCallFallbackMethod) {
                log.info("Resolution: Omnix will now call fallback method with name: {} defined in class with name: {}", fallbackMethodName, className);
            } else {
                if (!willCallFallbackMethod) {
                    log.info("Resolution: Omnix will NOT call fallback handler for white-listed value!");
                }
            }
            log.info("------------------------------------------------------------------------------------------------------------------");
        }
    }

    @Around(value = "methodWithFallbackHandlerInClassWithFallbackAdvice()")
    public Object aroundFallbackMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object originalReturnValue = joinPoint.proceed();
        Method method = getMethodFromSignature(joinPoint.getSignature());
        if(Objects.nonNull(method)){
            FallbackHandler handler = method.getAnnotation(FallbackHandler.class);
            FallbackAdvice fallbackAdvice = method.getDeclaringClass().getAnnotation(FallbackAdvice.class);
            if(Objects.nonNull(fallbackAdvice)){
                Class<?> methodClass = fallbackAdvice.value();
                Object methodClassObject = applicationContext.getBean(methodClass);
                if(shouldProceedWithInterception(originalReturnValue, Arrays.asList(handler.onValue()))){
                    Parameter[] parameters = method.getParameters();
                    Object[] methodArgs = joinPoint.getArgs();
                    Method fallbackMethod = getFallbackMethodByNameInClass(handler.methodName(), methodClass);
                    Object[] invocableMethodArgs = GenericDependencyInjector.resolveFallbackMethodArgumentsInjection(fallbackMethod, parameters, methodArgs);
                    return ReflectionUtils.invokeMethod(fallbackMethod, methodClassObject, invocableMethodArgs);
                }
            }
        }
        return originalReturnValue;
    }

    private boolean shouldProceedWithInterception(Object returningValue, List<String> blackListedValues){
        if(Objects.isNull(returningValue)){
            return blackListedValues.contains("null");
        }
        return blackListedValues.contains(String.valueOf(returningValue));
    }

    private Method getMethodFromSignature(Signature signature){
        return signature instanceof MethodSignature ? ((MethodSignature) signature).getMethod() : null;
    }

    private Method getFallbackMethodByNameInClass(String methodName, Class<?> clazz){
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.getName().equalsIgnoreCase(methodName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid method name in fallback handler. No method with name: %s found in class with name: %s", methodName, clazz.getName())));
    }

    public boolean shouldLogAspectOperation(Method method, Class<?> clazz){
        if(OmnixCoreApplicationUtil.anyNull(method, clazz)){
            return true;
        }
        if(Objects.nonNull(method.getAnnotation(RelaxAspectLogOperation.class))){
            return false;
        }else{
            return !Objects.nonNull(clazz.getAnnotation(RelaxAspectLogOperation.class));
        }
    }
}
