package cn.netty.server.typeServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @ClassName HeartSever
 * @Description TODO
 * @Author Ken
 * @Date DATE{TIME}
 **/
@Component
public class HeartSever {

    @Autowired
    @Qualifier("heartServerBootstrap")
    private ServerBootstrap serverBootstrap;

    @Autowired
    @Qualifier("heartSocketAddress")
    private InetSocketAddress tcpIpAndPort;

    private Channel serverChannel;

    //@PostConstruct（会导致其他东西不运行）
    public void start() throws Exception {
        serverChannel =  serverBootstrap.bind(tcpIpAndPort).sync().channel().closeFuture().sync().channel();
    }

    //@PreDestroy
    public void stop() throws Exception {
        serverChannel.close();
        serverChannel.parent().close();
    }

    public ServerBootstrap getServerBootstrap() {
        return serverBootstrap;
    }

    public void setServerBootstrap(ServerBootstrap serverBootstrap) {
        this.serverBootstrap = serverBootstrap;
    }

    public InetSocketAddress getTcpPort() {
        return tcpIpAndPort;
    }

    public void setTcpPort(InetSocketAddress tcpPort) {
        this.tcpIpAndPort = tcpPort;
    }
}

