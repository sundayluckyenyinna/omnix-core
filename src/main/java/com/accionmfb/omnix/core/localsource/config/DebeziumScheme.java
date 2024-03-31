package com.accionmfb.omnix.core.localsource.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebeziumScheme {
    private Object schema;
    private DebeziumPayload payload;
}
