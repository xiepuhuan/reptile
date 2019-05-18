package com.xiepuhuan.reptile.scheduler;

import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.scheduler.filter.impl.BloomRequestFilter;
import com.xiepuhuan.reptile.scheduler.filter.impl.HashSetRequestFilter;
import com.xiepuhuan.reptile.scheduler.impl.FIFOQueueScheduler;
import com.xiepuhuan.reptile.scheduler.impl.RedisFIFOQueueScheduler;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author xiepuhuan
 */
public class FIFOQueueSchedulerTest {

    private List<Request> requests;

    @Before
    public void setUp() {
        requests = new ArrayList<>();
        requests.add(new Request(Request.GET_METHOD, "http://www.baidu.com"));
        requests.add(new Request(Request.POST_METHOD, "http://www.baidu.com"));
        requests.add(new Request(Request.GET_METHOD, "http://www.baidu.com"));
        requests.add(new Request(Request.POST_METHOD, "http://www.baidu.com"));
        requests.add(new Request(Request.GET_METHOD, "https://www.baidu.com/s?wd=code"));
        requests.add(new Request(Request.POST_METHOD, "http://www.baidu.com/s").setBody("wd=code".getBytes()));
        requests.add(new Request(Request.GET_METHOD, "http://www.zhihu.com"));
        requests.add(new Request(Request.POST_METHOD, "http://www.zhihu.com"));
    }

    @Test
    public void test() {
        test(new FIFOQueueScheduler(new BloomRequestFilter()));
        test(new FIFOQueueScheduler(new HashSetRequestFilter()));
        test(new RedisFIFOQueueScheduler());
    }


    private void test(Scheduler scheduler) {

        scheduler.push(requests);
        Assert.assertEquals(6, scheduler.size());
    }
}
