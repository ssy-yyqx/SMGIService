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
 * HttpClient工具类
 *
 * @author ken
 */
public class HttpUtils {

    private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static CloseableHttpClient httpClient;
    private static PoolingHttpClientConnectionManager manager; // 连接池管理类
    private static ScheduledExecutorService monitorExecutor; // 监控
    private final static Object syncLock = new Object(); // 相当于线程锁,用于线程安全
    private static final int CONNECT_TIMEOUT = 10000;// 设置连接建立的超时时间为10s
    private static final int SOCKET_TIMEOUT = 10000;
    private static final int MAX_CONN = 100; // 最大连接数
    private static final int Max_PRE_ROUTE = 3000;
    private static final int MAX_ROUTE = 2000;

    /**
     * 对http请求进行基本设置
     *
     * @param httpRequestBase http请求
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
            // 多线程下多个线程同时调用getHttpClient容易导致重复创建httpClient对象的问题,所以加上了同步锁
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = createHttpClient(hostName, port);
                    // 开启监控线程,对异常和空闲线程进行关闭
                    monitorExecutor = Executors.newScheduledThreadPool(1);
                    monitorExecutor.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            // 关闭异常连接
                            manager.closeExpiredConnections();
                            // 关闭5s空闲的连接
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
     * 根据host和port构建httpclient实例
     *
     * @param host 要访问的域名
     * @param port 要访问的端口
     * @return
     */
    public static CloseableHttpClient createHttpClient(String host, int port) {
        ConnectionSocketFactory plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainSocketFactory).register("https", sslSocketFactory).build();
        manager = new PoolingHttpClientConnectionManager(registry);
        // 设置连接参数
        manager.setMaxTotal(MAX_CONN); // 最大连接数
        manager.setDefaultMaxPerRoute(Max_PRE_ROUTE); // 路由最大连接数
        HttpHost httpHost = new HttpHost(host, port);
        manager.setMaxPerRoute(new HttpRoute(httpHost), MAX_ROUTE);
        // 请求失败时,进行请求重试
        HttpRequestRetryHandler handler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
                if (i > 3) {
                    // 重试超过3次,放弃请求
                    logger.error("retry has more than 3 time, give up request");
                    return false;
                }
                if (e instanceof NoHttpResponseException) {
                    // 服务器没有响应,可能是服务器断开了连接,应该重试
                    logger.error("receive no response from server, retry");
                    return true;
                }
                if (e instanceof SSLHandshakeException) {
                    // SSL握手异常
                    logger.error("SSL hand shake exception");
                    return false;
                }
                if (e instanceof InterruptedIOException) {
                    // 超时
                    logger.error("InterruptedIOException");
                    return false;
                }
                if (e instanceof UnknownHostException) {
                    // 服务器不可达
                    logger.error("server host unknown");
                    return false;
                }
                if (e instanceof ConnectTimeoutException) {
                    // 连接超时
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
                    // 如果请求不是关闭连接的请求
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
            // 把文件转换成流对象FileBody
            FileBody bin = new FileBody(new File(localFile));

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // 相当于<input type="file" name="file"/>
            builder.addPart("multipartFile", bin);
            // 相当于<input type="text" name="userName" value=userName>
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
            // 发起请求 并返回请求的响应
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
     * 关闭连接池
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

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
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
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
                httpPost.setEntity(entity);
            }
            // 执行http请求
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
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpPost.setHeader(header.getKey(), header.getValue());
            }
            httpPost.setEntity(entity);
            // 执行http请求
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
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpPost.setHeader(header.getKey(), header.getValue());
            }
            httpPost.setEntity(entity);
            // 执行http请求
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