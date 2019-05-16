package com.xiepuhuan.reptile.scheduler;

import com.xiepuhuan.reptile.model.Request;
import java.util.Collection;

/**
 * @author xiepuhuan
 */
public interface Scheduler {

    void push(Request requests);

    void push(Request... requests);

    void push(Collection<Request> requests);

    Request poll();

    void put(Request requests) throws InterruptedException;

    void put(Request... requests) throws InterruptedException;

    void put(Collection<Request> requests) throws InterruptedException;

    Request take() throws InterruptedException;

    int size();
}
