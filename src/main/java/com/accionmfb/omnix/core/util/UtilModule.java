package com.accionmfb.omnix.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration

@Import({
        CommonUtil.class
})
public class UtilModule {
}
