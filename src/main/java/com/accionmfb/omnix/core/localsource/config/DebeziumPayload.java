package com.accionmfb.omnix.core.localsource.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebeziumPayload {
    private Map<String, Object> after;
}
