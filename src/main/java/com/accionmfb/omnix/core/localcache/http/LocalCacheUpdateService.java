package com.accionmfb.omnix.core.localcache.http;

import com.accionmfb.omnix.core.commons.ConfigSourceOperation;
import com.accionmfb.omnix.core.commons.ResponseCodes;
import com.accionmfb.omnix.core.event.ConfigSourcePropertyChangedEvent;
import com.accionmfb.omnix.core.payload.OmnixApiResponse;
import com.accionmfb.omnix.core.registry.LocalSourceCacheRegistry;
import com.accionmfb.omnix.core.service.DatasourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalCacheUpdateService {

    private final ApplicationEventPublisher publisher;
    private final DatasourceService datasourceService;

    public String saveNewConfigParams(LocalCacheDataRequest request){
        try{
            request.getPairs().forEach(localCachePair -> {
                try {
                    datasourceService.getParamValueOrDefault(localCachePair.getKey(), localCachePair.getValue());
                    publisher.publishEvent(ConfigSourcePropertyChangedEvent.of(this, String.valueOf(localCachePair.getKey()), localCachePair.getValue(), ConfigSourceOperation.SAVE));
                }catch (Exception exception){
                    log.info("Exception occurred while trying to save configuration params with key: {}. Exception message is: {}", localCachePair.getKey(), exception.getMessage());
                }
            });
            return "success";
        }catch (Exception exception){
            log.info("Exception occurred while trying to save configuration params. Exception message is: {}", exception.getMessage());
            return "Failed operation";
        }
    }

    public String updateConfigParams(LocalCacheDataRequest request){
        try {
            request.getPairs().forEach(localCachePair -> {
                try {
                    String key = localCachePair.getKey();
                    String value = localCachePair.getValue();
                    String newOrExistingValue = datasourceService.getParamValueOrDefault(key, value);
                    if (!newOrExistingValue.equalsIgnoreCase(value)) {
                        datasourceService.updateOmnixParam(key, value);
                    }
                    publisher.publishEvent(ConfigSourcePropertyChangedEvent.of(this, String.valueOf(key), value, ConfigSourceOperation.UPDATE));
                }catch (Exception exception){
                    log.info("Exception occurred while trying to update configuration params with key: {}. Exception message is: {}", localCachePair.getKey(), exception.getMessage());
                }
            });
        }catch (Exception exception){
            log.info("Exception occurred while trying to update configuration params. Exception message is: {}", exception.getMessage());
        }
        return "success";
    }

    public String deleteConfigParams(List<String> keys){
        keys.forEach(key -> {
            if(Objects.nonNull(key)){
                datasourceService.deleteOmnixParam(key);
            }
            publisher.publishEvent(ConfigSourcePropertyChangedEvent.of(this, String.valueOf(key), null, ConfigSourceOperation.DELETE));
        });
        return "success";
    }

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
