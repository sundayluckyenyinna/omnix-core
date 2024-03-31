package com.accionmfb.omnix.core.localsource.http;

import com.accionmfb.omnix.core.commons.ResponseCodes;
import com.accionmfb.omnix.core.payload.OmnixApiResponse;
import com.accionmfb.omnix.core.registry.LocalSourceCacheRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalCacheUpdateService {

    public OmnixApiResponse<List<LocalCachePair>> getApplicationConfigurationContext(){
        OmnixApiResponse<List<LocalCachePair>> response = new OmnixApiResponse<>();
        List<LocalCachePair> pairs = new ArrayList<>();
        Map<String, String> configurations = LocalSourceCacheRegistry.getConfigurationMap();
        configurations.forEach((key, value) -> {
            LocalCachePair pair = LocalCachePair.builder().key(key).value(value).build();
            pairs.add(pair);
        });
        response.setResponseCode(ResponseCodes.SUCCESS_CODE.getResponseCode());
        response.setResponseMessage("Success");
        response.setResponseData(pairs);
        return response;
    }
}
