package com.accionmfb.omnix.core.localcache.http;

import com.accionmfb.omnix.core.annotation.EncryptionPolicyAdvice;
import com.accionmfb.omnix.core.commons.EncryptionPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/local-cache")
public class LocalCacheUpdateController {

    private final LocalCacheUpdateService service;

    @EncryptionPolicyAdvice(EncryptionPolicy.RELAX_ALL)
    @PostMapping(value = "/post")
    public ResponseEntity<String> postLocalCacheKeyValueConfiguration(@RequestBody LocalCacheDataRequest dataRequest) {
        return ResponseEntity.ok(service.saveNewConfigParams(dataRequest));
    }

    @PutMapping(value = "/update")
    public ResponseEntity<String> updateLocalKeyValueConfiguration(@RequestBody LocalCacheDataRequest request){
        return ResponseEntity.ok(service.updateConfigParams(request));
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<String> deleteLocalKeyValueConfiguration(@RequestBody LocalCacheDeleteRequest request){
        return ResponseEntity.ok(service.deleteConfigParams(request.getKeys()));
    }
}
