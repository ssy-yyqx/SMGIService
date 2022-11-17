package cn.netty.client.channelInitializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @ClassName FileUoloadClientChannelInitializer
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
public class FileUploadClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    @Qualifier("fileUploadClientHandler")
    private SimpleChannelInboundHandler fileUploadClientHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        // 加入http的解码器
        p.addLast("http-decoder", new HttpRequestDecoder());
        // 加入ObjectAggregator解码器，作用是他会把多个消息转换为单一的FullHttpRequest或者FullHttpResponse
        p.addLast("http-aggregator", new HttpObjectAggregator(65536 * 10));
        // 加入http的编码器
        p.addLast("http-encoder", new HttpResponseEncoder());
        // 加入chunked 主要作用是支持异步发送的码流（大文件传输），但不专用过多的内存，防止java内存溢出
        p.addLast("http-chunked", new ChunkedWriteHandler());
        //自定义的handler
        //p.addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/ws", null, true, 65536 * 10));
        p.addLast("fileUploadServerHandler", fileUploadClientHandler);
    }
}
