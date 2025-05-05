package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.commons.OmnixParam;
import com.accionmfb.omnix.core.commons.ResponseCode;
import com.accionmfb.omnix.core.localsource.core.LocalParamStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityUtil {

    private final LocalParamStorage localParamStorage;
    private final HttpServletRequest httpServletRequest;

    private final static String APPLICATION_VERSION_HEADER_KEY = "newAppDetectionKey";

    public void validateClientVersion(){
        String versionToken = localParamStorage.getParamValueOrDefault(OmnixParam.MOBILE_APP_VERSION_TOKEN, "b9935837-a077-4911-854d-f9a92b384a96");
        String versionTokenHeader = httpServletRequest.getHeader(APPLICATION_VERSION_HEADER_KEY);
        if(CommonUtil.isNullOrEmpty(versionTokenHeader) || !versionTokenHeader.equalsIgnoreCase(versionToken)){
            throw HttpUtil.getResolvedException(ResponseCode.FAILED_MODEL, "Update to the newer app version to access this feature.");
        }
    }
}
