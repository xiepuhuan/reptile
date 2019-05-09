package com.xiepuhuan.reptile.downloader;

import com.xiepuhuan.reptile.downloader.impl.HttpClientDownloader;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.Response;
import java.io.IOException;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author xiepuhuan
 */

public class HttpClientDownloaderTest {

    private Downloader downloader = new HttpClientDownloader();

    @Test
    public void testDownload() {

        String[] urls = new String[] {
                "https://www.baidu.com",
                "https://www.jianshu.com",
                "http://www.zhihu.com",
                "http://www.jd.com"
        };


        for (String url : urls) {
            Request request = new Request(url);
            Response response = null;
            try {
                response = downloader.download(request);
                Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
                Assert.assertEquals(ContentType.TEXT_HTML.getMimeType(), response.getContent().getContentType().getMimeType());
                Assert.assertNotNull("Response headers", response.getHeaders());
                Assert.assertNotNull("Response contentType", response.getContent().getContentType());
                Assert.assertNotNull("Response body", response.getContent().getContent());
                Assert.assertNotNull("Response text content", response.getContent().getTextContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
