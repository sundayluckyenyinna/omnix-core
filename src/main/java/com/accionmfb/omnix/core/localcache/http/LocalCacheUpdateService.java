package com.accionmfb.omnix.core.localcache.http;

import com.accionmfb.omnix.core.service.DatasourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalCacheUpdateService {

    private final DatasourceService datasourceService;

    public String saveNewConfigParams(LocalCacheDataRequest request){
        request.getPairs().forEach(localCachePair -> datasourceService.saveOmnixParams(localCachePair.getKey(), localCachePair.getValue()));
        return "success";
    }

    public String updateConfigParams(LocalCacheDataRequest request){
        request.getPairs().forEach(localCachePair -> datasourceService.updateOmnixParam(localCachePair.getKey(), localCachePair.getValue()));
        return "success";
    }

    public String deleteConfigParams(List<String> keys){
        keys.forEach(datasourceService::deleteOmnixParam);
        return "success";
    }

}
