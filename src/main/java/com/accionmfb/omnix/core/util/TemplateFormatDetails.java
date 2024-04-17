package com.accionmfb.omnix.core.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateFormatDetails {
    private String outerHtml;
    private String formattedHtml;
    private String plainMessage;
}
