package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.commons.StringValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FileResourceCacheConfig {

    private static final String MAIL_FOLDER = "/mail/";
    private static final String MAIL_RESOURCES_PATH_MATCHING = "classpath*:/static/mail/**";

    @PostConstruct
    public void mailResourceResourceCacheApplicationRunner() {
        log.info("Starting to list mail resources");
        List<Resource> mailFileResources =
                FileUtilities.listFilesV2(MAIL_RESOURCES_PATH_MATCHING);
        mailFileResources.forEach(
                resource -> {
                    try {
                        String completePath = resource.getURL().getPath();
                        String relativeFilePath =
                                completePath
                                        .substring(completePath.indexOf(MAIL_FOLDER))
                                        .replace(MAIL_FOLDER, StringValues.EMPTY_STRING);
                        InputStream inputStream = resource.getInputStream();
                        String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        GlobalContextStore.save(
                                relativeFilePath,
                                content);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        log.info("Completely saved mail resources in the global context store");
    }
}
