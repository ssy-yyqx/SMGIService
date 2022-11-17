package cn.htht.service.platform.portal.component.config;

import cn.htht.service.platform.portal.component.web.service.TokenService;
import cn.htht.service.platform.portal.utils.core.redis.RedisCache;
import cn.htht.service.platform.portal.utils.enums.ChatTypeEnum;
import cn.htht.service.platform.portal.utils.enums.MessageTypeEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.Data;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * webSocket服务器
 */
@ServerEndpoint(value = "/upgrade/ws/asset/{userInfo}")
@Component
@RestController
@Data
@Api(tags = "websocket")
@RequestMapping("/websocket")
public class WebSocketServer {
    @Autowired
    TokenService tokenService;

    /**
     * 聊天记录
     */
    public static Map chatRecordMap = new HashMap();


    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 在线人数
     */
    public static int onlineNumber = 0;
    /**
     * 以用户的姓名为key，WebSocket为对象保存起来
     */
    private static Map<String, WebSocketServer> clients = new ConcurrentHashMap<>();
    private static Map<String, WebSocketServer> clientsAdmin = new ConcurrentHashMap<>();
    /**
     * 会话
     */
    private Session session;
    /**
     * 当前管理员与用户会话数量
     */
    private AtomicInteger chatNum = new AtomicInteger(0);
    /**
     * 当前用户名称
     */
    private String username;
    /**
     * 与当前用户对接管理员名称
     */
    private String adminName = null;
    /**
     * 与当前管理员对接用户列表
     */
    private Set<String> nameList = new HashSet<>();
    /**
     * 用户最后一次收发消息时间
     * */
    private AtomicLong userLastChatTime = new AtomicLong(-1);
    /**
     * 计时器
     * */
    private static ScheduledExecutorService scheduledExecutorService = null;
    /**
     * 管理员最大连接用户数
     * */
    private static final int MAX_CHAT_NUM = 16;

    @Autowired
    private RedisCache redisCache;

    private static Logger log = LoggerFactory.getLogger(WebSocketServer.class);
//    private static final AtomicInteger OnlineCount = new AtomicInteger(0);
//    // concurrent包的线程安全Set，用来存放每个客户端对应的Session对象。
//    private static CopyOnWriteArraySet<Session> SessionSet = new CopyOnWriteArraySet<Session>();

    @PostConstruct
    public void init() {
        System.out.println("websocket 加载");
    }

    private static void openIdleConnectionDetection(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                for (Map.Entry<String, WebSocketServer> m : clients.entrySet()) {
                    if((System.currentTimeMillis() - m.getValue().getUserLastChatTime().get() > (1000*60*3)) && m.getValue().getUserLastChatTime().get() != -1){
                        //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息 6代表长时间没有消息需要关闭连接
                        Map<String, Object> map1 = new HashMap();
                        map1.put("messageType", MessageTypeEnum.outOfTime.getKey());
                        //  返回用户名
                        map1.put("username", m.getKey());
                        //  发送信息
                        m.getValue().getSession().getBasicRemote().sendText(JSON.toJSONString(map1));
                    }
                }
                log.info("scheduledExecutorService");
            }
        }, 1,3, TimeUnit.MINUTES);
    }


    /**
     * 监听连接（有用户连接，立马到来执行这个方法）
     * session 发生变化
     *
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("userInfo") String userInfo, Session session) {
//        synchronized (WebSocketServer.class){
            increaseOnlineNumber();
            //把新用户名赋给变量
            String username = userInfo.split("@")[0];
            String chatType = userInfo.split("@")[1];
            this.username = username;
            //把新用户的 session 信息赋给变量
            this.session = session;
            //输出 websocket 信息
            logger.info("现在来连接的客户id：" + session.getId() + "用户名：" + username);
            logger.info("有新连接加入！ 当前在线人数" + onlineNumber);
            try {
                Set<String> lists;
                //管理员
                if (ChatTypeEnum.adminChatType.getKey().equalsIgnoreCase(chatType)) {
                    //把自己的信息加入到map当中去，this=当前类（把当前类作为对象保存起来）
                    clientsAdmin.put(username, this);
                    // 给自己发一条消息：告诉自己现在都有谁在线
                    Map<String, Object> map2 = new HashMap();
                    //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息 5代表系统消息
                    map2.put("messageType", MessageTypeEnum.onlineUsers.getKey());
                    //把所有用户放入map2
                    map2.put("onlineUsers", this.nameList);
                    //返回人数
                    map2.put("number", this.chatNum.get());
                    // 消息发送指定人（所有的在线用户信息）
                    sendMessageTo(JSON.toJSONString(map2), username);
                }
                //用户
                if (ChatTypeEnum.userChatType.getKey().equalsIgnoreCase(chatType)) {
                    //把自己的信息加入到map当中去，this=当前类（把当前类作为对象保存起来）
                    if (clients.isEmpty()){
                        openIdleConnectionDetection();
                    }
                    clients.put(username, this);
                    //获得所有的管理员
                    lists = clientsAdmin.keySet();
                    WebSocketServer admin = null;
                    for (Iterator<String> iterator = lists.iterator();iterator.hasNext();){
                        WebSocketServer adminTemp = clientsAdmin.get(iterator.next());
                        int currentChatNum = adminTemp.getChatNum().get();
                        logger.info(adminTemp.getUsername() + " " + currentChatNum);
                        if (currentChatNum == 0){
                            admin = adminTemp;
                            break;
                        }else if (currentChatNum < MAX_CHAT_NUM){
                            logger.info(adminTemp.getUsername() + " currentChatNum:" + currentChatNum + " MAX_CHAT_NUM:" + MAX_CHAT_NUM);
                            admin = adminTemp;
                        }
                    }
                    if (admin == null){
                        // 给自己发一条消息：告诉自己现在客服繁忙
                        Map<String, Object> map2 = new HashMap();
                        //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息
                        map2.put("messageType", MessageTypeEnum.busy.getKey());
                        map2.put("textMessage", "客服繁忙，请稍后重试.");
                        // 消息发送指定人（所有的在线用户信息）
                        sendMessageTo(JSON.toJSONString(map2), username);
//                        clients.remove(username);
                        return;
                    }

                    this.adminName = admin.getUsername();
                    logger.info(Arrays.toString(admin.getNameList().toArray()));
                    admin.nameListAdd(username);
                    logger.info(Arrays.toString(admin.getNameList().toArray()));
                    admin.getChatNum().incrementAndGet();
                    // 给管理员发送上线通知
                    //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息
                    Map<String, Object> map1 = new HashMap();
                    //  把所有用户列表
                    map1.put("onlineUsers", admin.getNameList());
                    //  返回上线状态
                    map1.put("messageType", MessageTypeEnum.online.getKey());
                    //  返回用户名
                    map1.put("username", username);
//                        //  返回在线人数
//                        map1.put("number", onlineNumber);
                    //  发送上线信息
                    sendMessageTo(JSON.toJSONString(map1), admin.getUsername());

                    logger.info("上线通知");

                    // 给自己发一条消息：告诉自己现在都有谁在线
                    Map<String, Object> map2 = new HashMap();
                    //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息
                    map2.put("messageType", MessageTypeEnum.onlineUsers.getKey());
                    //把管理员用户放入map2
                    map2.put("onlineUsers", admin.getUsername());
                    // 消息发送指定人（所有的在线用户信息）
                    sendMessageTo(JSON.toJSONString(map2), username);
                }
            } catch (IOException e) {
                logger.info(username + "上线的时候发生了错误" + "-onOpen");
            }

//        }
    }

    public void reconnect(String username){
//        synchronized (WebSocketServer.class){
            try {
                Set<String> lists = null;
                WebSocketServer admin = null;
                //获得所有的管理员
                lists = clientsAdmin.keySet();
                for (Iterator<String> iterator = lists.iterator();iterator.hasNext();){
                    WebSocketServer adminTemp = clientsAdmin.get(iterator.next());
                    int currentChatNum = adminTemp.getChatNum().get();
                    logger.info(adminTemp.getUsername() + " " + currentChatNum);
                    if (currentChatNum == 0){
                        admin = adminTemp;
                        break;
                    }else if (currentChatNum < MAX_CHAT_NUM){
                        logger.info(adminTemp.getUsername() + " currentChatNum:" + currentChatNum + " MAX_CHAT_NUM:" + MAX_CHAT_NUM);
                        admin = adminTemp;
                    }
                }
                if (admin == null){
                    // 给自己发一条消息：告诉自己现在客服繁忙
                    Map<String, Object> map2 = new HashMap();
                    //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息
                    map2.put("messageType", MessageTypeEnum.busy.getKey());
                    map2.put("textMessage", "客服繁忙，请稍后片刻...");
                    // 消息发送指定人（所有的在线用户信息）
                    sendMessageTo(JSON.toJSONString(map2), username);
//                    clients.remove(username);
                    return;
                }
                clients.get(username).setAdminName(admin.getUsername());
                logger.info(Arrays.toString(admin.getNameList().toArray()));
                admin.nameListAdd(username);
                logger.info(Arrays.toString(admin.getNameList().toArray()));
                admin.getChatNum().incrementAndGet();
                // 给管理员发送上线通知
                //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息
                Map<String, Object> map1 = new HashMap();
                //  把所有用户列表
                map1.put("onlineUsers", admin.getNameList());
                //  返回上线状态
                map1.put("messageType", MessageTypeEnum.online.getKey());
                //  返回用户名
                map1.put("username", username);
//                        //  返回在线人数
//                        map1.put("number", onlineNumber);
                //  发送上线信息
                sendMessageTo(JSON.toJSONString(map1), admin.getUsername());

                logger.info("上线通知");

                // 给自己发一条消息：告诉自己现在都有谁在线
                Map<String, Object> map2 = new HashMap();
                //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息
                map2.put("messageType", MessageTypeEnum.onlineUsers.getKey());
                //把管理员用户放入map2
                map2.put("onlineUsers", admin.getUsername());
                // 消息发送指定人（所有的在线用户信息）
                sendMessageTo(JSON.toJSONString(map2), username);
            }catch (IOException e){
                logger.info(username + "上线的时候发生了错误" + "-reconnect");
            }
//        }
    }

    public synchronized void nameListAdd(String username) {
        this.nameList.add(username);
    }


    /**
     * 监听连接断开（有用户退出，会立马到来执行这个方法）
     */
    @OnClose
    public void onClose(@PathParam("userInfo") String userInfo) {
        List<String> reconUserList = new ArrayList<>();
        synchronized (WebSocketServer.class){
            decreaseOnlineNumber();
            String chatType = userInfo.split("@")[1];
            try {
                //messageType 1代表上线 2代表下线 3代表在线名单  4代表普通消息
                Map<String, Object> map1 = new HashMap();
                map1.put("messageType", MessageTypeEnum.offline.getKey());
                //管理员
                if (ChatTypeEnum.adminChatType.getKey().equalsIgnoreCase(chatType)) {
                    //所有在线用户中去除下线用户
                    clientsAdmin.remove(username);
                    //所有在线用户
                    for (Iterator<String> iterator = nameList.iterator(); iterator.hasNext(); ) {
                        String next = iterator.next();
                        WebSocketServer user = clients.get(next);
                        if (user == null) {
                            System.out.println(next);
                        }
                        user.setAdminName(null);
                        sendMessageTo(JSON.toJSONString(map1), user.getUsername());
                        log.info(user.getUsername());
                        reconUserList.add(user.getUsername());
                    }
                }
                //用户
                if (ChatTypeEnum.userChatType.getKey().equalsIgnoreCase(chatType)) {
                    if (adminName != null) {
                        WebSocketServer admin = clientsAdmin.get(adminName);
                        admin.nameListRemove(username);
                        admin.getChatNum().decrementAndGet();
                        //所有在线用户
                        map1.put("onlineUsers", admin.getNameList());
                        //下线用户的用户名
                        map1.put("username", username);
                        //发送下线通知
                        logger.info(map1.toString());
                        sendMessageTo(JSON.toJSONString(map1), adminName);
                    }
                    //所有在线用户中去除下线用户
                    clients.remove(username);
                    if (clients.isEmpty()){
                        scheduledExecutorService.shutdownNow();
                        scheduledExecutorService = null;
                    }
                }
            } catch (IOException e) {
                logger.info(username + "下线的时候发生了错误");
            }
            logger.info("有连接关闭！ 当前在线人数" + onlineNumber);
        }
        reconUserList.forEach(this::reconnect);
    }

    private synchronized void nameListRemove(String username) {
        this.nameList.remove(username);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("服务端发生了错误" + error.getMessage());
        //error.printStackTrace();
    }

    /**
     * 监听消息（收到客户端的消息立即执行）
     *
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            logger.info("来自客户端消息：" + message + "客户端的id是：" + session.getId());
            //用户发送的信息
            JSONObject jsonObject = JSON.parseObject(message);
            //发送的内容
            String textMessage = jsonObject.getString("message");
            //发送人
            String fromusername = jsonObject.getString("username");
            //接收人  to=all 发送消息给所有人 || to= !all   to == 用户名
            String tousername = jsonObject.getString("to");
            //消息类型
            String messageType = jsonObject.getString("messageType");
            if (messageType != null && messageType.equals(String.valueOf(MessageTypeEnum.change.getKey()))){
                synchronized (WebSocketServer.class){
                    String oldName = jsonObject.getString("oldName");
                    String newName = jsonObject.getString("newName");
                    clients.put(newName,clients.get(oldName));
                    clients.remove(oldName);
                    String adminName = clients.get(newName).getAdminName();
                    clients.get(newName).setUsername(newName);
                    Map<String, Object> map1 = new HashMap();
                    map1.put("messageType", MessageTypeEnum.change.getKey());
                    map1.put("oldName", oldName);
                    map1.put("newName", newName);
                    map1.put("tousername", adminName);
                    sendMessageTo(JSON.toJSONString(map1), adminName);
                    return;
                }
            }

            //发送消息  -- messageType 1代表上线 2代表下线 3代表在线名单  4代表消息
            Map<String, Object> map1 = new HashMap();
            map1.put("messageType", MessageTypeEnum.message.getKey());
            map1.put("textMessage", textMessage);
            map1.put("fromusername", fromusername);
            if ("All".equals(tousername)) {
                //消息发送所有人（同步）
                map1.put("tousername", "所有人");
                sendMessageAll(JSON.toJSONString(map1), fromusername);
            } else {
                //消息发送指定人（同步）
                map1.put("tousername", tousername);
                sendMessageTo(JSON.toJSONString(map1), tousername);
            }
        } catch (Exception e) {
            logger.info("发生了错误了");
        }
    }

    /**
     * 消息发送指定人
     */
    public void sendMessageTo(String message, String ToUserName) throws IOException {
        //遍历所有用户
        for (WebSocketServer item : clients.values()) {
            if (item.username.equals(ToUserName)) {
                //消息发送指定人（同步）
                item.session.getBasicRemote().sendText(message);
                break;
            }
        }
        //遍历所有管理员
        for (WebSocketServer item : clientsAdmin.values()) {
            if (item.username.equals(ToUserName)) {
                //消息发送指定人（同步）
                item.session.getBasicRemote().sendText(message);
                break;
            }
        }
        getUserLastChatTime().set(System.currentTimeMillis());
        logger.info("sendMessageTo() " + getUsername() + "-" + getUserLastChatTime().get() + " " + ToUserName + " " + message);
    }

    /**
     * 消息发送所有人
     */
    public void sendMessageAll(String message, String fromUserName) throws IOException {
        for (WebSocketServer item : clients.values()) {
            //消息发送所有人（同步）getAsyncRemote
            item.session.getBasicRemote().sendText(message);
        }
        for (WebSocketServer item : clientsAdmin.values()) {
            //消息发送所有人（同步）getAsyncRemote
            item.session.getBasicRemote().sendText(message);
        }
    }

    public static synchronized int getOnlineNumber() {
        return onlineNumber;
    }

    public static synchronized void increaseOnlineNumber() {
        onlineNumber++;
    }

    public static synchronized void decreaseOnlineNumber() {
        onlineNumber--;
    }

    public static Map<String, WebSocketServer> getClients(){
        return clients;
    }

    public static Map<String, WebSocketServer> getClientsAdmin(){
        return clientsAdmin;
    }


}
