package com.flowable.config;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.common.api.delegate.event.FlowableEvent;
import org.flowable.engine.common.api.delegate.event.FlowableEventListener;
import org.flowable.engine.common.impl.cfg.TransactionState;
import org.springframework.stereotype.Component;

/**
 * @author zhs
 * @Description
 * @createTime 2021/4/22 0022 14:08
 */

@Slf4j
public class LocalFlowListener implements FlowableEventListener {

    @Override
    public void onEvent(FlowableEvent flowableEvent) {
        log.info("FlowableEvent type:{}", flowableEvent.getType().name());
    }

    @Override
    public boolean isFailOnException() {
        return true;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return true;
    }

    @Override
    public String getOnTransaction() {
        //事务提交后触发
        return TransactionState.COMMITTED.name();
    }
}
