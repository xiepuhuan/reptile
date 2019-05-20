package com.xiepuhuan.reptile.downloader.impl;

import com.xiepuhuan.reptile.common.pool.Pool;
import com.xiepuhuan.reptile.common.pool.impl.FixedPool;
import com.xiepuhuan.reptile.config.DownloaderConfig;
import com.xiepuhuan.reptile.downloader.CloseableDownloader;
import com.xiepuhuan.reptile.downloader.constants.UserAgentConstants;
import com.xiepuhuan.reptile.downloader.model.Proxy;
import com.xiepuhuan.reptile.model.Content;
import com.xiepuhuan.reptile.model.Request;
import com.xiepuhuan.reptile.model.Response;
import com.xiepuhuan.reptile.utils.ArgUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.UnsupportedCharsetException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.SSLContext;

/**
 * @author xiepuhuan
 */
public class HttpClientDownloader implements CloseableDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientDownloader.class);

    private final Pool<String> userAgentPool;

    private final Pool<Proxy> proxyPool;

    private final CookieStore cookieStore;

    private final DownloaderConfig config;

    private final CloseableHttpClient httpClient;

    private volatile PoolingHttpClientConnectionManager clientConnectionManager;

    public HttpClientDownloader() {
        this(DownloaderConfig.DEFAULT_DOWNLOADER_CONFIG);
    }

    public HttpClientDownloader(DownloaderConfig config) {
        ArgUtils.notNull(config, "DownloaderConfig");
        this.userAgentPool = config.isEnableUserAgentPoolConfig() ? new FixedPool<>(config.getUserAgentPoolConfig()) : null;
        this.proxyPool = config.isEnableProxyPoolConfig() ? new FixedPool<>(config.getProxyPoolConfig()) : null;
        this.config = config;
        this.cookieStore = new BasicCookieStore();
        this.clientConnectionManager = buildConnectionManager();
        this.httpClient = buildHttpClient();
    }

    public static HttpClientDownloader create(DownloaderConfig config) {
        return new HttpClientDownloader(config);
    }

    @Override
    public Response download(Request request) throws ParseException, UnsupportedCharsetException, IOException {
        if (request == null) {
            return null;
        }

        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies()).forEach(cookieStore::addCookie);
        }

        RequestBuilder requestBuilder = RequestBuilder.create(request.getMethod())
                .setUri(request.getUrl())
                .setHeader("cookie", "d_c0=\"AFDiaIA5ng2PTgyuejWKI0qwUESDkyWifXk=|1526736573\"; _zap=f87e6049-36e6-4461-b497-36c543a42485; __gads=ID=c5aaa62037f66581:T=1547031642:S=ALNI_MYC8p9OWvWR7nnsd3vM0b3RODGm_g; __utmv=51854390.100--|2=registration_date=20160811=1^3=entry_date=20160811=1; _xsrf=87c7f562-07c1-4187-8852-29a8732b245c; q_c1=aafc3877d9d14425a8302bb2444da49b|1557479793000|1526736578000; __utma=51854390.1355650844.1554915899.1554915899.1558253321.2; __utmc=51854390; __utmz=51854390.1558253321.2.2.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; tst=h; anc_cap_id=efa677707ebd432fbd72de947968ea69; capsion_ticket=\"2|1:0|10:1558277537|14:capsion_ticket|44:MTBkNjcxYjBkMjhjNGVmYWEyNTM2YTMzMGZjODM5M2Y=|4255cc0232411ebf74da07625c85febad95f4117fec6ede656eb655779493ece\"; z_c0=\"2|1:0|10:1558277545|4:z_c0|92:Mi4xTXdOWUF3QUFBQUFBVU9Kb2dEbWVEU1lBQUFCZ0FsVk5xYnZPWFFCOFdLYkZkRVlIUW9hdGpPN3doT1gxWlJTSjNn|c9d7b62f1d2f127980f35405818a91f6de7caf1a66d01d13a4110abeea52641c\"; tgw_l7_route=4860b599c6644634a0abcd4d10d37251");

        if (proxyPool != null) {
            requestBuilder.setConfig(RequestConfig.custom().setProxy(proxyPool.selectOne().buildHttpHost()).build());
        }

        if (userAgentPool != null) {
            request.setHeader(UserAgentConstants.USER_AGENT_NAME, userAgentPool.selectOne());
        }

        if (request.getHeaders() != null) {
            request.getHeaders().forEach(requestBuilder::addHeader);
        }

        if (request.getBody() != null) {
            requestBuilder.setEntity(EntityBuilder.create()
                    .setContentType(request.getContentType())
                    .setBinary(request.getBody())
                    .build()
            );
        }

        if (clientConnectionManager == null) {
            throw new IOException("HttpClientDownloader has been closed");
        }

        CloseableHttpResponse httpResponse = httpClient.execute(requestBuilder.build());

        return new Response(httpResponse.getStatusLine().getStatusCode(), httpResponse.getAllHeaders())
                .setContent(parse(httpResponse.getEntity()));
    }

    private Content parse(HttpEntity entity) throws ParseException, UnsupportedCharsetException, IOException {

        try {
            byte[] body = toByteArray(entity);

            if (!ArgUtils.notEmpty(body)) {
                return null;
            }

            return new Content(ContentType.getOrDefault(entity), body);
        } catch (ParseException | UnsupportedCharsetException | IOException e) {
            LOGGER.warn("Failed Get the contents of an InputStreamas a byte[]: {}", e.getMessage());
            throw e;
        }
    }

    private byte[] toByteArray(HttpEntity entity) throws IOException {
        if (entity == null || entity.getContent() == null) {
            return null;
        }

        try (InputStream inputStream = entity.getContent()) {

            int capacity = Math.max(4096, Math.max((int) entity.getContentLength(), inputStream.available()));

            final ByteArrayOutputStream buffer = new ByteArrayOutputStream(capacity);
            final byte[] tmp = new byte[capacity];
            int l;
            while ((l = inputStream.read(tmp)) != -1) {
                buffer.write(tmp, 0, l);
            }
            return buffer.toByteArray();
        }
    }

    private CloseableHttpClient buildHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(config.getSocketTimeout())
                .setConnectTimeout(config.getConnectTimeout())
                .setConnectionRequestTimeout(config.getConnectRequestTimeout())
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                .setUserAgent(config.getGeneralUserAgent())
                .setMaxConnPerRoute(config.getDefaultMaxPerRoute())
                .setMaxConnTotal(config.getMaxTotalConnections())
                .setDefaultHeaders(config.getHeaders())
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .setConnectionManager(clientConnectionManager)
                .setProxy(config.getGeneralProxy() != null ? config.getGeneralProxy().buildHttpHost() : null);

        return httpClientBuilder.build();
    }

    private PoolingHttpClientConnectionManager buildConnectionManager() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", buildSSLConnectionSocketFactory())
                .build();

        clientConnectionManager = new PoolingHttpClientConnectionManager(registry);
        clientConnectionManager.setDefaultSocketConfig(SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build());
        clientConnectionManager.setDefaultMaxPerRoute(config.getDefaultMaxPerRoute());
        return clientConnectionManager;
    }

    private SSLConnectionSocketFactory buildSSLConnectionSocketFactory() {
        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, (chain, authType) -> true).build();

            return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | RuntimeException e) {
            LOGGER.warn("Failed to build SSLConnectionSocketFactory");
        }

        return SSLConnectionSocketFactory.getSocketFactory();
    }

    @Override
    public void close() {
        if (clientConnectionManager != null) {
            synchronized (this) {
                if (clientConnectionManager != null) {
                    try {
                        clientConnectionManager = null;
                        httpClient.close();
                        LOGGER.info("HttpClientDownloader has been successfully closed");
                    } catch (IOException e) {
                        LOGGER.warn("Failed to close httpClient: {}", e.getMessage());
                    }
                }
            }
        }
    }
}
