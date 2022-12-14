package cn.netty.server.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

;

/**
 * HttpClient?????????
 *
 * @author ken
 */
public class HttpUtils {

    private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static CloseableHttpClient httpClient;
    private static PoolingHttpClientConnectionManager manager; // ??????????????????
    private static ScheduledExecutorService monitorExecutor; // ??????
    private final static Object syncLock = new Object(); // ??????????????????,??????????????????
    private static final int CONNECT_TIMEOUT = 10000;// ????????????????????????????????????10s
    private static final int SOCKET_TIMEOUT = 10000;
    private static final int MAX_CONN = 100; // ???????????????
    private static final int Max_PRE_ROUTE = 3000;
    private static final int MAX_ROUTE = 2000;

    /**
     * ???http????????????????????????
     *
     * @param httpRequestBase http??????
     */
    private static void setRequestConfig(HttpRequestBase httpRequestBase) {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECT_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httpRequestBase.setConfig(requestConfig);
    }

    public static CloseableHttpClient getHttpClient(String url) {
        String hostName = url.split("/")[2];
        // System.out.println(hostName);
        int port = 80;
        if (hostName.contains(":")) {
            String[] args = hostName.split(":");
            hostName = args[0];
            port = Integer.parseInt(args[1]);
        }
        if (httpClient == null) {
            // ????????????????????????????????????getHttpClient????????????????????????httpClient???????????????,????????????????????????
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = createHttpClient(hostName, port);
                    // ??????????????????,????????????????????????????????????
                    monitorExecutor = Executors.newScheduledThreadPool(1);
                    monitorExecutor.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            // ??????????????????
                            manager.closeExpiredConnections();
                            // ??????5s???????????????
                            manager.closeIdleConnections(2000, TimeUnit.MILLISECONDS);
                            logger.debug("close expired and idle for over 5s connection");
                        }
                    }, 3000, 3000, TimeUnit.MILLISECONDS);
                }
            }
        }
        return httpClient;
    }

    /**
     * ??????host???port??????httpclient??????
     *
     * @param host ??????????????????
     * @param port ??????????????????
     * @return
     */
    public static CloseableHttpClient createHttpClient(String host, int port) {
        ConnectionSocketFactory plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainSocketFactory).register("https", sslSocketFactory).build();
        manager = new PoolingHttpClientConnectionManager(registry);
        // ??????????????????
        manager.setMaxTotal(MAX_CONN); // ???????????????
        manager.setDefaultMaxPerRoute(Max_PRE_ROUTE); // ?????????????????????
        HttpHost httpHost = new HttpHost(host, port);
        manager.setMaxPerRoute(new HttpRoute(httpHost), MAX_ROUTE);
        // ???????????????,??????????????????
        HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                if (i > 3) {
                    // ????????????3???,????????????
                    logger.error("retry has more than 3 time, give up request");
                    return false;
                }
                if (e instanceof NoHttpResponseException) {
                    // ?????????????????????,?????????????????????????????????,????????????
                    logger.error("receive no response from server, retry");
                    return true;
                }
                if (e instanceof SSLHandshakeException) {
                    // SSL????????????
                    logger.error("SSL hand shake exception");
                    return false;
                }
                if (e instanceof InterruptedIOException) {
                    // ??????
                    logger.error("InterruptedIOException");
                    return false;
                }
                if (e instanceof UnknownHostException) {
                    // ??????????????????
                    logger.error("server host unknown");
                    return false;
                }
                if (e instanceof ConnectTimeoutException) {
                    // ????????????
                    logger.error("Connection Time out");
                    return false;
                }
                if (e instanceof SSLException) {
                    logger.error("SSLException");
                    return false;
                }
                HttpClientContext context = HttpClientContext.adapt(httpContext);
                HttpRequest request = context.getRequest();
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    // ???????????????????????????????????????
                    return true;
                }
                return false;
            }
        };
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(manager).setRetryHandler(handler)
                .build();
        return client;
    }


    public static String uploadFile(String url, String localFile, String fileParamName, Map<String, String> params) {
        HttpPost httpPost = new HttpPost(url);
        setRequestConfig(httpPost);
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // ???????????????????????????FileBody
            FileBody bin = new FileBody(new File(localFile));

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // ?????????<input type="file" name="file"/>
            builder.addPart("multipartFile", bin);
            // ?????????<input type="text" name="userName" value=userName>
            if (StringUtils.isNotBlank(fileParamName)) {
                builder.addPart("filesFileName",
                        new StringBody(fileParamName, ContentType.create("text/plain", Consts.UTF_8)));
            }
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.addPart(key,
                            new StringBody(params.get(key), ContentType.create("text/plain", Consts.UTF_8)));
                }
            }

            HttpEntity reqEntity = builder.build();
            httpPost.setEntity(reqEntity);
            httpPost.setHeader("USER_ID", "1");
            httpPost.setHeader("COMPANY_ID", null);
            // ???????????? ????????????????????????
            response = getHttpClient(url).execute(httpPost, HttpClientContext.create());
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }


    /**
     * ???????????????
     */
    public static void closeConnectionPool() {
        try {
            httpClient.close();
            manager.close();
            monitorExecutor.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> headers = new ConcurrentHashMap<>();

    public static String doGet(String url, Map<String, String> param) {

        // ??????Httpclient??????
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // ??????uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // ??????http GET??????
            HttpGet httpGet = new HttpGet(uri);

            // ????????????
            response = httpclient.execute(httpGet);
            // ???????????????????????????200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static String doGet(String url) {
        return doGet(url, null);
    }


    public static String doPost(String url, Map<String, String> param) {
        // ??????Httpclient??????
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // ??????Http Post??????
            HttpPost httpPost = new HttpPost(url);
            // ??????????????????
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // ????????????
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
                httpPost.setEntity(entity);
            }
            // ??????http??????
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public static String doPost(String url) {
        return doPost(url, null);
    }

    public static String doPostJson(String url, String json) {
        // ??????Httpclient??????
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // ??????Http Post??????
            HttpPost httpPost = new HttpPost(url);
            // ??????????????????
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpPost.setHeader(header.getKey(), header.getValue());
            }
            httpPost.setEntity(entity);
            // ??????http??????
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static CloseableHttpResponse doPostJsonResponse(String url, String json) {
        // ??????Httpclient??????
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // ??????Http Post??????
            HttpPost httpPost = new HttpPost(url);
            // ??????????????????
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpPost.setHeader(header.getKey(), header.getValue());
            }
            httpPost.setEntity(entity);
            // ??????http??????
            response = httpClient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    public static Integer doPutFile(String url, Map<String, String> header, List<MultipartFile> file) throws IOException {
        HttpPut httpPut = new HttpPut(url);
        CloseableHttpResponse response = null;
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpPut.setHeader(entry.getKey(), entry.getValue());
            }
        }
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(file.get(0).getBytes(), ContentType.DEFAULT_BINARY);
        httpPut.setEntity(byteArrayEntity);
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        try {
            response = closeableHttpClient.execute(httpPut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.getStatusLine().getStatusCode();
    }

    public static Integer doPutFile(String url, Map<String, String> header, InputStream stream) throws IOException {
        HttpPut httpPut = new HttpPut(url);
        CloseableHttpResponse response = null;
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpPut.setHeader(entry.getKey(), entry.getValue());
            }
        }
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(stream.readAllBytes(), ContentType.DEFAULT_BINARY);
        httpPut.setEntity(byteArrayEntity);
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        try {
            response = closeableHttpClient.execute(httpPut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.getStatusLine().getStatusCode();
    }
}