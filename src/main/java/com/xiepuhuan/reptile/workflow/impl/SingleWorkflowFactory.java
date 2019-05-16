package com.xiepuhuan.reptile.workflow.impl;

import com.xiepuhuan.reptile.config.ReptileConfig;
import com.xiepuhuan.reptile.workflow.Workflow;
import com.xiepuhuan.reptile.workflow.WorkflowFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiepuhuan
 */
public class SingleWorkflowFactory implements WorkflowFactory {

    private final CountDownLatch finalization;

    private final AtomicInteger activeThreadCount;

    private final Object requestArrived;

    private final ReptileConfig config;

    private final AtomicInteger number;

    private final String namePrefix = "Single-workflow-";

    public SingleWorkflowFactory(CountDownLatch finalization, ReptileConfig config) {
        this.finalization = finalization;
        this.activeThreadCount = new AtomicInteger(0);
        this.requestArrived = new Object();
        this.config = config;
        number = new AtomicInteger(1);
    }

    @Override
    public Workflow newWorkflow() {
        return new SingleWorkflow(finalization, activeThreadCount, requestArrived, namePrefix + number.getAndIncrement(), config);
    }
}
