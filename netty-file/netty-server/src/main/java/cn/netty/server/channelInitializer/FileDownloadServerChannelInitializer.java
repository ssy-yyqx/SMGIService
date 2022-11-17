package cn.netty.server.channelInitializer;


import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import cn.netty.common.protobuf.Message.MessageBase;
import org.springframework.web.HttpRequestHandler;

import java.util.concurrent.TimeUnit;

@Component(value = "fileDownloadServerChannelInitializer")
public class FileDownloadServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    @Qualifier("fileDownloadServerHandler")
    private ChannelInboundHandlerAdapter fileDownloadServerHandler;
    
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
    	ChannelPipeline p = socketChannel.pipeline();
        // 加入http的解码器
        p.addLast("http-decoder", new HttpRequestDecoder());
        // 加入ObjectAggregator解码器，作用是他会把多个消息转换为单一的FullHttpRequest或者FullHttpResponse
        p.addLast("http-aggregator", new HttpObjectAggregator(65536));
        // 加入http的编码器
        p.addLast("http-encoder", new HttpResponseEncoder());
        // 加入chunked 主要作用是支持异步发送的码流（大文件传输），但不专用过多的内存，防止java内存溢出
        p.addLast("http-chunked", new ChunkedWriteHandler());
        //自定义的handler
        p.addLast("fileDownloadServerHandler", fileDownloadServerHandler);
    }
}
