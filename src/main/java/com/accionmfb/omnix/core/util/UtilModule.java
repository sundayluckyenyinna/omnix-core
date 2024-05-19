package com.accionmfb.omnix.core.util;

import com.accionmfb.omnix.core.util.pdf.FlyingSaucerPdfUtility;
import com.accionmfb.omnix.core.util.pdf.OpenPdfUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration

@Import({
        CommonUtil.class,
        OpenPdfUtility.class,
        FlyingSaucerPdfUtility.class
})
public class UtilModule {
}
