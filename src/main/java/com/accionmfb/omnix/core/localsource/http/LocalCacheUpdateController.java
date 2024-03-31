package com.accionmfb.omnix.core.localsource.http;

import com.accionmfb.omnix.core.payload.OmnixApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/local-cache")
public class LocalCacheUpdateController {

    private final LocalCacheUpdateService service;

    @GetMapping(value = "/configuration-pairs")
    public ResponseEntity<OmnixApiResponse<List<LocalCachePair>>> getLocalCacheConfigurationPairs(){
        return ResponseEntity.ok(service.getApplicationConfigurationContext());
    }
}
