package com.accionmfb.omnix.core.event.data;

import com.accionmfb.omnix.core.commons.ConfigSourceOperation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ConfigSourcePropertyChangedEvent extends ApplicationEvent {
    private String paramKey;
    private String paramValue;
    private ConfigSourceOperation operation;


    public static ConfigSourcePropertyChangedEvent of(Object source, String paramKey, String paramValue, ConfigSourceOperation operation){
        return new ConfigSourcePropertyChangedEvent(source, paramKey, paramValue, operation);
    }

    public ConfigSourcePropertyChangedEvent(Object source, String paramKey, String paramValue, ConfigSourceOperation operation){
        this(source);
        this.paramKey = paramKey;
        this.paramValue = paramValue;
        this.operation = operation;
    }

    public ConfigSourcePropertyChangedEvent(Object source) {
        super(source);
    }

}
