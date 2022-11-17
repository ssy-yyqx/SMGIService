package cn.netty.server.handler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WebSocketHandler.java
 * @Description 类描述
 * @Author YangZiJie
 * @Version 1.0.0
 * @Date 2022年07月22日
 */
@Component(value = "webSocketHandler")
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    public Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * 定义channel组
     * */
    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("{} 与客户端建立连接，通道开启！", channel.remoteAddress());

        //添加到channelGroup通道组
        CHANNEL_GROUP.add(channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("{} 与客户端断开连接，通道关闭！", channel.remoteAddress());
        //添加到channelGroup 通道组
        CHANNEL_GROUP.remove(channel);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //首次连接是FullHttpRequest
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();

            Map paramMap=getUrlParams(uri);
            log.info("接收到的参数是：{}", JSON.toJSONString(paramMap));
            //如果url包含参数，需要处理
            if(uri.contains("?")){
                String newUri=uri.substring(0,uri.indexOf("?"));
                log.info("newUri: {}", newUri);
                request.setUri(newUri);
            }

        }else if(msg instanceof TextWebSocketFrame){
            //正常的TEXT消息类型
            TextWebSocketFrame frame=(TextWebSocketFrame)msg;
            log.info("客户端收到服务器数据：{}", frame.text());
            sendAllMessage(frame.text());
        }
        super.channelRead(ctx, msg);
    }

    private void sendAllMessage(String message){
        //收到信息后，群发给所有channel
        CHANNEL_GROUP.writeAndFlush( new TextWebSocketFrame(message));
    }

    private static Map getUrlParams(String url){
        Map<String,String> map = new HashMap<>();
        url = url.replace("?",";");
        if (!url.contains(";")){
            return map;
        }
        if (url.split(";").length > 0){
            String[] arr = url.split(";")[1].split("&");
            for (String s : arr){
                String key = s.split("=")[0];
                String value = s.split("=")[1];
                map.put(key,value);
            }
            return  map;

        }else{
            return map;
        }
    }
}
