package com.xiepuhuan.reptile.workflow.impl;

import com.xiepuhuan.reptile.config.ReptileConfig;
import com.xiepuhuan.reptile.workflow.Workflow;
import com.xiepuhuan.reptile.workflow.WorkflowFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xiepuhuan
 */
public class DistributedWorkflowFactory implements WorkflowFactory {

    private final CountDownLatch finalization;

    private final ReptileConfig config;

    private final AtomicInteger number;

    private final String namePrefix = "Distributed-workflow-";

    public DistributedWorkflowFactory(CountDownLatch finalization, ReptileConfig config) {
        this.finalization = finalization;
        this.config = config;
        this.number = new AtomicInteger(1);
    }

    @Override
    public Workflow newWorkflow() {
        return new DistributedWorkflow(finalization,namePrefix + number.getAndIncrement(), config);
    }
}
