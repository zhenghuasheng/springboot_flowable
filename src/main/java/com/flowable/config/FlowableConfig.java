package com.flowable.config;

import org.flowable.engine.common.api.delegate.event.FlowableEventListener;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhs
 * @Description
 * @createTime 2021/4/22 0022 11:05
 */
@Configuration
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        engineConfiguration.setActivityFontName("宋体");
        engineConfiguration.setLabelFontName("宋体");
        engineConfiguration.setAnnotationFontName("宋体");

        List<FlowableEventListener> listeners = new ArrayList<>(3);
        listeners.add(getLocalListener());

        engineConfiguration.setEventListeners(listeners);
    }

    @Bean
    public LocalFlowListener getLocalListener() {
        return new LocalFlowListener();
    }
}